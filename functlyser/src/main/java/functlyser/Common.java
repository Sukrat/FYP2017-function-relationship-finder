package functlyser;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Common {
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static List<String> getMessagesFromError(Errors error) {
        return error.getAllErrors()
                .stream()
                .map(m -> {
                    String objectName = m.getObjectName();
                    String fieldName = ".";
                    if (m instanceof FieldError) {
                        fieldName += ((FieldError) m).getField();
                    }
                    return format("%s%s: %s", objectName, fieldName, m.getDefaultMessage());
                }).collect(Collectors.toList());
    }
}
