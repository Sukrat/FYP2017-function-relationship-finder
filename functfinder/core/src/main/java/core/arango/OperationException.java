package core.arango;

public class OperationException extends RuntimeException {

    public OperationException(String message) {
        super(message);
    }

    public OperationException(String format, Object... args) {
        super(String.format(format, args));
    }
}
