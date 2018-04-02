package webapp.websocket;

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

    public CommandExecutorWithWebsocket create(String profile) {
        return new CommandExecutorWithWebsocket(profile, messageSendingOperations);
    }
}
