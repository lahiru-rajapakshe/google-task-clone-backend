package lk.lahiru.servletbackend.tasks.util;

public class ResponseStatusException extends RuntimeException{

    private int status;

    public ResponseStatusException(int status, String message) {
        super(message);
        this.status = status;
    }

    public ResponseStatusException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
