package webapp.websocket;

import core.command.IProgress;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import webapp.controller.messages.WebSocketMessage;

public class WebSocketProgress implements IProgress {

    private String id;
    private int outOf = 0;
    private int done = 0;
    private String message = "Process Started";
    private String topic;
    private SimpMessageSendingOperations messageSendingOperations;

    public WebSocketProgress(String id,
                             String topic,
                             SimpMessageSendingOperations messageSendingOperations) {
        this.id = id;
        this.topic = topic;
        this.messageSendingOperations = messageSendingOperations;
    }

    @Override
    public void update(int done, int outOf) {
        send(done, outOf, message);
    }

    @Override
    public void update(String message) {
        send(done, outOf, message);
    }

    @Override
    public void update(String format, Object... args) {
        send(done, outOf, String.format(format, args));
    }

    @Override
    public void update(int done, int outOf, String message) {
        send(done, outOf, message);
    }

    @Override
    public void update(int done, int outOf, String format, Object... args) {
        send(done, outOf, String.format(format, args));
    }

    private void send(int done, int outOf, String message) {
        this.done = done;
        this.outOf = outOf;
        this.message = message;
        messageSendingOperations
                .convertAndSend(topic, new WebSocketMessage(id, outOf, done, message));
    }
}