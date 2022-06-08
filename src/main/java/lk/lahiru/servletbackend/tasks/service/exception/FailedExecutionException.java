package lk.lahiru.servletbackend.tasks.service.exception;

public class FailedExecutionException extends RuntimeException{

    public FailedExecutionException() {
        super();
    }

    public FailedExecutionException(String message) {
        super(message);
    }

    public FailedExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
