package test.upgrade.vincent.workers;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import test.upgrade.vincent.availabilities.CacheableAvailability;
import test.upgrade.vincent.reservations.ReservationService;
import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;
import test.upgrade.vincent.validators.ReservationValidatorService;

@Service
@Slf4j
public class ReservationActionServiceImpl implements ReservationActionService {

    private final CacheableAvailability cacheAvailability;
    private final ReservationValidatorService validationService;
    private final ReservationService reservationService;

    public ReservationActionServiceImpl(@Autowired CacheableAvailability cacheAvailability,
                                        @Autowired ReservationValidatorService validationService,
                                        @Autowired ReservationService reservationService) {
        this.cacheAvailability = cacheAvailability;
        this.validationService = validationService;
        this.reservationService = reservationService;
    }

    @Override
    synchronized public Reservation performAction(Reservation reservation, ReservationAction action) throws Exception {
        switch (action) {
            case CREATE:
                return performCreate(reservation);
            case UPDATE:
                return performUpdate(reservation);
            case CANCEL:
                return performCancel(reservation);
        }
        return null;
    }

    /**
     * Verify the reservation can be created
     * if yes: create the reservation
     *
     * @param reservation
     * @return the new Reservation freshly created
     * @throws Exception when does not validate Validator rules
     */
    private Reservation performCreate(Reservation reservation) throws Exception {
        boolean isValid = this.validationService.performValidation(reservation);
        if (!isValid) {
            throw new Exception("The combination of start-end date is not valid");
        }

        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            if (!this.cacheAvailability.isAvailable(date)) {
                throw new Exception("The combination of start-end date is not available");
            }
        }

        reservation.setCreatedOn(LocalDate.now());
        this.reservationService.addReservation(reservation);
        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            this.cacheAvailability.evictAvailability(date);
        }

        return reservation;
    }

    /**
     * Verify the reservation can be updated
     * if yes: update the reservation
     *
     * @param reservation
     * @return the Reservation freshly updated
     * @throws Exception when does not validate Validator rules
     */
    private Reservation performUpdate(Reservation reservation) throws Exception {
        boolean isValid = this.validationService.performValidation(reservation);
        if (!isValid) {
            throw new Exception("The combination of start-end date is not valid");
        }

        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            if (!this.cacheAvailability.isAvailable(date)) {
                throw new Exception("The combination of start-end date is not available");
            }
        }

        Reservation current = this.reservationService.getReservationById(reservation.getId()).get();
        reservation = this.reservationService.updateReservation(reservation);

        for (LocalDate date = current.getStartDate(); date.isBefore(current.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            this.cacheAvailability.evictAvailability(date);
        }
        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            this.cacheAvailability.evictAvailability(date);
        }

        return reservation;
    }

    /**
     * Cancel a Reservation
     *
     * @param reservation
     * @return the Reservation freshly deleted
     */
    private Reservation performCancel(Reservation reservation) {

        Reservation current = this.reservationService.getReservationById(reservation.getId()).get();
        for (LocalDate date = current.getStartDate(); date.isBefore(current.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            this.cacheAvailability.evictAvailability(date);
        }
        this.reservationService.cancelReservation(reservation);

        return reservation;
    }
}
