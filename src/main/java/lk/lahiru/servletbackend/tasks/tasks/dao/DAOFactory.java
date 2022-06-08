package lk.lahiru.servletbackend.tasks.tasks.dao;

import lk.ijse.dep8.tasks.dao.custom.impl.QueryDAOImpl;
import lk.ijse.dep8.tasks.dao.custom.impl.TaskDAOImpl;
import lk.ijse.dep8.tasks.dao.custom.impl.TaskListDAOImpl;
import lk.ijse.dep8.tasks.dao.custom.impl.UserDAOImpl;

import java.sql.Connection;

public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory(){

    }

    public static DAOFactory getInstance(){
        return (daoFactory == null)? (daoFactory = new DAOFactory()): daoFactory;
    }

    public <T extends SuperDAO> T getDAO(Connection connection, DAOTypes daoType){
        switch (daoType){
            case USER:
                return (T) new UserDAOImpl(connection);
            case TASK_LIST:
                return (T) new TaskListDAOImpl(connection);
            case TASK:
                return (T) new TaskDAOImpl(connection);
            case QUERY:
                return (T) new QueryDAOImpl(connection);
            default:
                return null;
        }
    }

    public enum DAOTypes{
        USER, TASK_LIST, TASK, QUERY
    }
}
