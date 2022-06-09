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
    synchronized public Reservation performAction(Reservation reservation, ReservationAction action) {
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

    private Reservation performCreate(Reservation reservation) {
        boolean isValid = this.validationService.performValidation(reservation);
        if (!isValid) {
            log.warn("Failed during creation of new reservation for user: {}, reason: not validated", reservation.getUserEmail());
            return null;
        }

        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            if (!this.cacheAvailability.isAvailable(date)) {
                log.warn("Failed during creation of new reservation for user: {}, reason: {} not available", reservation.getUserEmail(), date);
                return null;
            }
        }

        try {
            reservation.setCreatedOn(LocalDate.now());
            this.reservationService.addReservation(reservation);
            for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
                this.cacheAvailability.evictAvailability(date);
            }
        } catch (Exception e) {
            log.error("Failed during creation of new reservation for user: {}, reason: {}", reservation.getUserEmail(), e.getMessage());
            return null;
        }

        return reservation;
    }

    private Reservation performUpdate(Reservation reservation) {
        boolean isValid = this.validationService.performValidation(reservation);
        if (!isValid) {
            log.warn("Failed during update of reservationId: {}, reason: not validated", reservation.getId());
            return null;
        }

        for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
            if (!this.cacheAvailability.isAvailable(date)) {
                log.warn("Failed during update of reservationId: {}, reason: {} not available", reservation.getId(), date);
                return null;
            }
        }
        try {
            Reservation current = this.reservationService.getReservationById(reservation.getId());
            reservation = this.reservationService.updateReservation(reservation);

            for (LocalDate date = current.getStartDate(); date.isBefore(current.getEndDate().plusDays(1)); date = date.plusDays(1)) {
                this.cacheAvailability.evictAvailability(date);
            }
            for (LocalDate date = reservation.getStartDate(); date.isBefore(reservation.getEndDate().plusDays(1)); date = date.plusDays(1)) {
                this.cacheAvailability.evictAvailability(date);
            }
        } catch (Exception e) {
            log.error("Failed during creation of new reservation for user: {}, reason: {}", reservation.getUserEmail(), e.getMessage());
            return null;
        }
        return reservation;
    }

    private Reservation performCancel(Reservation reservation) {
        try {
            Reservation current = this.reservationService.getReservationById(reservation.getId());
            this.reservationService.cancelReservation(reservation);
            for (LocalDate date = current.getStartDate(); date.isBefore(current.getEndDate().plusDays(1)); date = date.plusDays(1)) {
                this.cacheAvailability.evictAvailability(date);
            }
            return reservation;
        } catch (Exception e) {
            log.error("Failed during cancellation of reservationId: {}, reason: {}", reservation.getId(), e.getMessage());
            return null;
        }
    }
}
