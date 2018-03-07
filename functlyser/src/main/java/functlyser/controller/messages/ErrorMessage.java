package functlyser.controller.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorMessage {

    private int statusCode;
    private String type;
    private List<String> messages;

    public ErrorMessage(int statusCode, String type, List<String> messages) {
        this.statusCode = statusCode;
        this.type = type;
        this.messages = messages;
    }

    public ErrorMessage(int statusCode, String type, String message) {
        this(statusCode, type, Arrays.asList(message));
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getType() {
        return type;
    }

    public List<String> getMessages() {
        return messages;
    }
}
