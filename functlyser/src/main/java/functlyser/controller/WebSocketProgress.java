package functlyser.controller;

import functlyser.command.CommandProgess;
import functlyser.controller.messages.WebSocketMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

public class WebSocketProgress implements CommandProgess {

    private String id;
    private int totalWork = 0;
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
    public void setTotalWork(int totalWork) {
        sendTotalWorkDone(totalWork, message);
    }

    @Override
    public void setTotalWork(int totalWork, String message) {
        sendTotalWorkDone(totalWork, message);
    }

    @Override
    public void setTotalWork(int totalWork, String format, Object... args) {
        sendTotalWorkDone(totalWork, String.format(format, args));
    }

    @Override
    public void update(int done) {
        sendUpdates(done, message);
    }

    @Override
    public void update(String message) {
        sendUpdates(done, message);
    }

    @Override
    public void update(String format, Object... args) {
        sendUpdates(done, String.format(format, args));
    }

    @Override
    public void update(int done, String message) {
        sendUpdates(done, message);
    }

    @Override
    public void update(int done, String format, Object... args) {
        sendUpdates(done, String.format(format, args));
    }

    private void sendTotalWorkDone(int totalWork, String message) {
        this.totalWork = totalWork;
        this.message = message;
        send();
    }

    private void sendUpdates(int done, String message) {
        this.done = done;
        this.message = message;
        send();
    }

    private void send() {
        messageSendingOperations
                .convertAndSend(topic, new WebSocketMessage(id, totalWork, done, message));
    }
}