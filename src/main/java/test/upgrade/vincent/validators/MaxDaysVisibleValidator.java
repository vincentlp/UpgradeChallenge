package test.upgrade.vincent.validators;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import test.upgrade.vincent.reservations.models.Reservation;

@Component
public class MaxDaysVisibleValidator extends Validator {

    public boolean isValid(Reservation reservation) {
        boolean isNotTooLate =  reservation.getStartDate().compareTo(LocalDate.now()) >= 1;
        boolean isNotTooEarly = reservation.getEndDate().compareTo(LocalDate.now().plusMonths(1)) <= 0;

        return isNotTooLate && isNotTooEarly && super.isValid(reservation);
    }
}
