package webapp.websocket;

import core.command.ICommand;
import core.command.ICommandExecutor;
import core.command.IProgress;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.UUID;

public class CommandExecutorWithWebsocket implements ICommandExecutor {

    private String profile;
    private SimpMessageSendingOperations messageSendingOperations;

    public CommandExecutorWithWebsocket(String profile,
                                        SimpMessageSendingOperations messageSendingOperations) {
        this.profile = profile;
        this.messageSendingOperations = messageSendingOperations;
    }

    @Override
    public <T> T execute(ICommand<T> command) {
        IProgress webSocketProgress =
                new WebSocketProgress(UUID.randomUUID().toString(), "/reply/" + profile, messageSendingOperations);
        return command.execute(webSocketProgress);
    }
}
