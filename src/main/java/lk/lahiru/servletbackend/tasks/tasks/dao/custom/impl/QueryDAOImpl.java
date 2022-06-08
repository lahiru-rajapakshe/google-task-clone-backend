package lk.lahiru.servletbackend.tasks.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.custom.QueryDAO;

import java.sql.Connection;

public class QueryDAOImpl implements QueryDAO {

    private final Connection connection;

    public QueryDAOImpl(Connection connection) {
        this.connection = connection;
    }

}
