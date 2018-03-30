package core.service;

public class ServiceException extends RuntimeException {
    public ServiceException(String s) {
        super(s);
    }

    public ServiceException(String format, Object... args) {
        super(String.format(format, args));
    }
}
