package functlyser.controller.messages;

public class ErrorMessage {

    private int statusCode;
    private String type;
    private String message;

    public ErrorMessage(int statusCode, String type, String message) {
        this.statusCode = statusCode;
        this.type = type;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
