package functlyser.model.validator;

import functlyser.model.Profile;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.stream.IntStream;

import static functlyser.Common.isNullOrEmpty;
import static java.lang.String.format;

public class ProfileValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Profile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Profile profile = (Profile) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required",
                "Profile name cannot be empty!");
        if (isNullOrEmpty(profile.getName())) {
            errors.rejectValue("name", "field.required",
                    "Profile name cannot be empty!");
        } else if (profile.getName().length() > 60) {
            errors.rejectValue("name", "field.length.over",
                    "Profile name must be less than 60 characters!");
        }

        if (profile.getColumns() == null || profile.getColumns().size() < 2) {
            errors.rejectValue("columns", "field.required",
                    "Profile must contain atleast 2 column!");
        } else if (isKeyNullOrEmpty(profile)) {
            errors.rejectValue("columns", "field.required",
                    "Profile Column name cannot be empty!");
        } else if (!isInOrder(profile)) {
            errors.rejectValue("columns", "field.not.in.order",
                    "Profile Columns must be in order!");
        } else if (!profile.getColumns().containsKey(profile.getOutputColumn())) {
            errors.rejectValue("outputColumn", "field.not.present",
                    format("Profile output column '%s' is not present in the column list!", profile.getOutputColumn()));
        }
    }

    private boolean isInOrder(Profile profile) {
        int[] sorted = profile.getColumns().values().stream()
                .mapToInt(m -> m.getIndex())
                .sorted()
                .toArray();
        int[] range = IntStream.range(0, sorted.length)
                .toArray();
        return Arrays.equals(sorted, range);
    }

    private boolean isKeyNullOrEmpty(Profile profile) {
        return profile.getColumns().keySet()
                .stream()
                .anyMatch((key) -> isNullOrEmpty(key));
    }
}

