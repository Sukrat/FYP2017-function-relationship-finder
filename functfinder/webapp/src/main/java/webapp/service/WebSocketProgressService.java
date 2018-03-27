package webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import webapp.controller.WebSocketProgress;

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
