package test.upgrade.vincent.validators;

import test.upgrade.vincent.reservations.models.Reservation;

public class Validator {
    Validator nextValidator;

    boolean isValid(Reservation reservation) {
        if (this.nextValidator != null) {
            return this.nextValidator.isValid(reservation);
        }
        return true;
    }

    void setNextValidator(Validator validator) {
        this.nextValidator = validator;
    }
}
