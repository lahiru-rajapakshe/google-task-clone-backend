package lk.lahiru.servletbackend.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.CrudDAOImpl;
import lk.ijse.dep8.tasks.dao.custom.TaskListDAO;
import lk.ijse.dep8.tasks.entity.TaskList;
import org.hibernate.Session;

public class TaskListDAOImpl extends CrudDAOImpl<TaskList, Integer> implements TaskListDAO {

    public TaskListDAOImpl(Session session) {
        this.session = session;
    }
}
