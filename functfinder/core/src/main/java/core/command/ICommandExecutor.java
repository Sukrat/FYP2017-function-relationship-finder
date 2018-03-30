package core.command;

public interface ICommandExecutor {
    <T> T execute();
}
