package cmdapp;

import core.command.ICommand;
import core.command.ICommandExecutor;

public class CmdCommandExecutor implements ICommandExecutor {
    @Override
    public <T> T execute(ICommand<T> command) {
        return command.execute(new CmdProgress());
    }
}
