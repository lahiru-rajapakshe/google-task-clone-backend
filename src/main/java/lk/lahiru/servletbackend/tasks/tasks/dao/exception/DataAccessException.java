package lk.lahiru.servletbackend.tasks.tasks.dao.exception;

public class DataAccessException extends RuntimeException{

    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }
}
