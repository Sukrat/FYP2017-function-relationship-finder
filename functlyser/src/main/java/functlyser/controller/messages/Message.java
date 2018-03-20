package functlyser.controller.messages;

import java.util.Arrays;
import java.util.List;

public class Message {

    private int statusCode;

    private String message;


    public Message(String message) {
        this(200, message);
    }

    public Message(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
