package cmdapp;

import core.command.ICommand;
import core.command.ICommandExecutor;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor implements ICommandExecutor {
    @Override
    public <T> T execute(ICommand<T> command) {
        return command.execute(new CmdProgress());
    }
}
