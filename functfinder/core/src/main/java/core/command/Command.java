package core.command;

public interface Command<TParam, TResult> {

    TResult execute(CommandProgess progress, TParam param);
}
