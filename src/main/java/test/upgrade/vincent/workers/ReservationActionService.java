package test.upgrade.vincent.workers;

import test.upgrade.vincent.reservations.models.Reservation;
import test.upgrade.vincent.reservations.models.ReservationAction;

public interface ReservationActionService {

    Reservation performAction(Reservation reservation, ReservationAction action) throws Exception;
}
