package functlyser.model.validator;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ValidatorRunner<T extends Validator> {

    private T validator;

    public ValidatorRunner(T validator) {
        this.validator = validator;
    }

    public Errors validate(Object target) {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(target, target.getClass().getSimpleName());
        ValidationUtils.invokeValidator(validator, target, result);
        return result;
    }
}
