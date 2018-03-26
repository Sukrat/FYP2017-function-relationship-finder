package functlyser.controller.messages;

public class WebSocketMessage {
    private String id;
    private int totalWork;
    private int done;
    private String message;

    public WebSocketMessage(String id, int totalWork, int done, String message) {
        this.id = id;
        this.totalWork = totalWork;
        this.done = done;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public int getTotalWork() {
        return totalWork;
    }

    public int getDone() {
        return done;
    }

    public String getMessage() {
        return message;
    }
}
