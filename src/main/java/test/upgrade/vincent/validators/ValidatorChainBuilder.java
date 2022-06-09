package test.upgrade.vincent.validators;

public class ValidatorChainBuilder {

    private Validator first;
    private Validator last;

    public ValidatorChainBuilder() {
        this.first = null;
        this.last = null;
    }

    public ValidatorChainBuilder add(Validator validator) {
        if (this.first == null) {
            this.first = validator;
            this.last = validator;
            return this;
        }
        this.last.setNextValidator(validator);
        this.last = validator;
        return this;
    }

    public Validator getFirst() {
        return this.first;
    }
}
