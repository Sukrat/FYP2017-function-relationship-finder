package webapp.service;

public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String format, Object... args) {
        super(String.format(format, args));
    }
}
