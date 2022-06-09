package test.upgrade.vincent.validators;

import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import test.upgrade.vincent.reservations.models.Reservation;

@Component
@Slf4j
public class MaxDaysValidator extends Validator {

    private final int maxDays = 3;


    public boolean isValid(Reservation reservation) {
        if (ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate()) <= maxDays) {
            return super.isValid(reservation);
        }else{
            return false;
        }
        //return ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate()) <= maxDays && super.isValid(reservation);
    }
}
