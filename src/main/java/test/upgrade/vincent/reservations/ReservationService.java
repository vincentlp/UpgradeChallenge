package test.upgrade.vincent.reservations;

import java.time.LocalDate;
import java.util.Optional;

import test.upgrade.vincent.reservations.models.Reservation;

public interface ReservationService {

    Optional<Reservation> getReservationById(Long id);

    Optional<Reservation> getReservationByDate(LocalDate valueDate);

    Reservation addReservation(Reservation reservation);

    Reservation updateReservation(Reservation reservation);

    Reservation cancelReservation(Reservation reservation);
}
