package functlyser.service;

import functlyser.controller.WebSocketProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebSocketProgressService {


    private SimpMessageSendingOperations messageSendingOperations;

    @Autowired
    public WebSocketProgressService(SimpMessageSendingOperations messageSendingOperations) {
        this.messageSendingOperations = messageSendingOperations;
    }

    public WebSocketProgress create(String url) {
        return new WebSocketProgress(UUID.randomUUID().toString(), url, messageSendingOperations);
    }
}
