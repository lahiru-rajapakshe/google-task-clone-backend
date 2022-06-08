package lk.lahiru.servletbackend.tasks.service.util;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface ExecutionContext {
    Logger logger = Logger.getLogger(ExecutionContext.class.getName());

    static void execute(ExecutionContext e) {
        try {
            e.context();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), e);
        }
    }

    void context() throws SQLException;
}
