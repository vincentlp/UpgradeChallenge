package test.upgrade.vincent.reservations;

import java.time.LocalDate;

import test.upgrade.vincent.reservations.models.Reservation;

public interface ReservationService {

    Reservation getReservationById(Long id);

    Reservation getReservationByDate(LocalDate valueDate);

    Reservation addReservation(Reservation reservation);

    Reservation updateReservation(Reservation reservation);

    Reservation cancelReservation(Reservation reservation);
}
