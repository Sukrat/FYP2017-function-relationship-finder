package core.command;

public interface ICommandExecutor {
    <T> T execute(ICommand<T> command);
}
