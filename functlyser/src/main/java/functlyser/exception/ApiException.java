package functlyser.exception;

import java.util.Arrays;
import java.util.List;

public class ApiException extends RuntimeException {

    private int statusCode;

    private List<String> messages;

    public ApiException(int statusCode, List<String> messages) {
        this.statusCode = statusCode;
        this.messages = messages;
    }

    public ApiException(int statusCode, String message) {
        this(statusCode, Arrays.asList(message));
    }

    public ApiException(List<String> message) {
        this(400, message);
    }

    public ApiException(String message) {
        this(400, message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getMessages() {
        return messages;
    }
}
