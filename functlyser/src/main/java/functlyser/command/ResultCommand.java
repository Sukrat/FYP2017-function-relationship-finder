package functlyser.command;

public interface ResultCommand<TParam, TResult> {

    TResult execute(TParam param);
}
