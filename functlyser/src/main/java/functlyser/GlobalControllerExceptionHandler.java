package functlyser;


import functlyser.controller.messages.ErrorMessage;
import functlyser.exception.ApiException;
import functlyser.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(HttpServletRequest req, Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(500, e.getClass().getSimpleName(), e.getMessage());
        return returnErrorMessage(errorMessage);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorMessage> handleApiException(HttpServletRequest req, Exception e) {
        ApiException apiException = (ApiException) e;
        ErrorMessage errorMessage = new ErrorMessage(apiException.getStatusCode(),
                apiException.getClass().getSimpleName(), apiException.getMessages());
        return returnErrorMessage(errorMessage);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> handleValidationException(HttpServletRequest req, Exception e) {
        ValidationException apiException = (ValidationException) e;
        ErrorMessage errorMessage = new ErrorMessage(400,
                apiException.getClass().getSimpleName(), apiException.getMessages());
        return returnErrorMessage(errorMessage);
    }

    private ResponseEntity<ErrorMessage> returnErrorMessage(ErrorMessage errorMessage) {
        return ResponseEntity.status(errorMessage.getStatusCode()).body(errorMessage);
    }
}
