package lk.lahiru.servletbackend.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.custom.QueryDAO;
import org.hibernate.Session;

public class QueryDAOImpl implements QueryDAO {

    private final Session session;

    public QueryDAOImpl(Session session) {
        this.session = session;
    }

}
