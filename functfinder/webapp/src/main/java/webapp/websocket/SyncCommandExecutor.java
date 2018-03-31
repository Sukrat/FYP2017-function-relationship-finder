package webapp.websocket;

import core.command.ICommand;
import core.command.ICommandExecutor;
import org.springframework.stereotype.Component;
import webapp.websocket.EmptyProgress;

@Component
public class SyncCommandExecutor implements ICommandExecutor {

    @Override
    public <T> T execute(ICommand<T> command) {
        return command.execute(new EmptyProgress());
    }
}
