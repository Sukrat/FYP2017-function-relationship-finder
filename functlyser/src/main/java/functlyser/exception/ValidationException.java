package functlyser.exception;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class ValidationException extends RuntimeException {

    private Errors error;

    public ValidationException(Errors error) {
        this.error = error;
    }

    public List<String> getMessages() {
        if (error == null) {
            return Arrays.asList();
        }

        return error.getAllErrors()
                .stream()
                .map(m -> {
                    String fieldName = "";
                    if (m instanceof FieldError) {
                        fieldName = "." + ((FieldError) m).getField();
                    }
                    return format("%s%s: %s", m.getObjectName(), fieldName, m.getDefaultMessage());
                }).collect(Collectors.toList());
    }
}
