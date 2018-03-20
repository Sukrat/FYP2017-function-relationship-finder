package functlyser.command;

public interface Command<TParam, TResult> {

    TResult execute(CommandProgess progress, TParam param);
}
