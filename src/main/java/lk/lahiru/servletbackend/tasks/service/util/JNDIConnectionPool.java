package lk.lahiru.servletbackend.tasks.service.util;

import lk.ijse.dep8.tasks.service.exception.FailedExecutionException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JNDIConnectionPool {

    private static JNDIConnectionPool jndiConnectionPool;

    private JNDIConnectionPool(){}

    public static JNDIConnectionPool getInstance() {
        return (jndiConnectionPool == null)? (jndiConnectionPool = new JNDIConnectionPool()): jndiConnectionPool;
    }

    public DataSource getDataSource(){
        try {
            InitialContext ctx = new InitialContext();
            return (DataSource) ctx.lookup("java:comp/env/jdbc/pool");
        } catch (NamingException e) {
            throw new FailedExecutionException("Failed to lookup the pool", e);
        }
    }
}
