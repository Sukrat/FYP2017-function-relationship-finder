package functlyser;

import java.util.Arrays;
import java.util.List;

public class Message {

    private int statusCode;

    private List<String> messages;


    public Message(String message) {
        this(200, Arrays.asList(message));
    }

    public Message(List<String> message) {
        this(200, message);
    }

    public Message(int statusCode, List<String> messages) {
        this.statusCode = statusCode;
        this.messages = messages;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public List<String> getMessages() {
        return messages;
    }
}
