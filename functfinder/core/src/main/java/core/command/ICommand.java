package core.command;

public interface ICommand<T> {
    T execute(IProgress progress);
}
