package webapp;


import core.command.CommandException;
import core.service.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import webapp.controller.messages.ErrorMessage;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(HttpServletRequest req, Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(500, e.getClass().getSimpleName(), e.getMessage());
        return returnErrorMessage(errorMessage);
    }

    @ExceptionHandler(CommandException.class)
    public ResponseEntity<ErrorMessage> handleCommandException(HttpServletRequest req, Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(400, e.getClass().getSimpleName(), e.getMessage());
        return returnErrorMessage(errorMessage);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorMessage> handleServiceException(HttpServletRequest req, Exception e) {
        ErrorMessage errorMessage = new ErrorMessage(400, e.getClass().getSimpleName(), e.getMessage());
        return returnErrorMessage(errorMessage);
    }

    private ResponseEntity<ErrorMessage> returnErrorMessage(ErrorMessage errorMessage) {
        return ResponseEntity.status(errorMessage.getStatusCode()).body(errorMessage);
    }
}
