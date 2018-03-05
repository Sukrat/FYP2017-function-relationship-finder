package functlyser.model.validator;

import functlyser.model.Data;
import functlyser.model.Profile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;

public class DataValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Data.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Data data = (Data) target;

        if (data.getProfileId() == null) {
            errors.rejectValue("profileId", "field.required",
                    "ProfileId of data cannot be null");
        }
        if (isNullOrEmptyString(data.getFileName())) {
            errors.rejectValue("fileName", "field.required",
                    "ExcelFileName of data cannot be null");
        }
        if (data.getColumns() == null || data.getColumns().size() < 2) {
            errors.rejectValue("columns", "field.required",
                    "Data must contain atleast 2 columns!");
        } else if (isKeyNullOrEmpty(data)) {
            errors.rejectValue("columns", "field.required",
                    "Data Column name cannot be empty!");
        }
    }

    private boolean isKeyNullOrEmpty(Data data) {
        return data.getColumns().keySet()
                .stream()
                .anyMatch((key) -> isNullOrEmptyString(key));
    }
}
