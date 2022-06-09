package lk.lahiru.servletbackend.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.CrudDAOImpl;
import lk.ijse.dep8.tasks.dao.custom.TaskDAO;
import lk.ijse.dep8.tasks.entity.Task;
import org.hibernate.Session;

public class TaskDAOImpl extends CrudDAOImpl<Task, Integer> implements TaskDAO {

    public TaskDAOImpl(Session session) {
        this.session = session;
    }
}
