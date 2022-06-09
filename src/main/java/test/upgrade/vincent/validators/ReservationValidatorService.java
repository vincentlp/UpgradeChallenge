package test.upgrade.vincent.validators;

import org.springframework.stereotype.Service;

import test.upgrade.vincent.reservations.models.Reservation;

@Service
public class ReservationValidatorService {

    private ValidatorChainBuilder chainBuilder;

    public ReservationValidatorService() {
        this.chainBuilder = new ValidatorChainBuilder()
                .add(new MaxDaysValidator())
                .add(new MaxDaysVisibleValidator());
    }

    public boolean performValidation(Reservation reservation) {
        return this.chainBuilder.getFirst().isValid(reservation);
    }
}
