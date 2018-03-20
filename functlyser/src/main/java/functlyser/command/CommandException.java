package functlyser.command;

public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String format, Object... args) {
        super(String.format(format, args));
    }
}
