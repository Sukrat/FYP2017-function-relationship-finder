package cmdapp;

public class CmdException extends RuntimeException {

    public CmdException(String message) {
        super(message);
    }

    public CmdException(String format, Object... args) {
        super(String.format(format, args));
    }
}
