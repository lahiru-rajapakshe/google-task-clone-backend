package lk.lahiru.servletbackend.tasks.dao.custom.impl;

import lk.ijse.dep8.tasks.dao.custom.TaskDAO;
import lk.ijse.dep8.tasks.dao.exception.DataAccessException;
import lk.ijse.dep8.tasks.entity.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDAOImpl implements TaskDAO {

    private final Connection connection;

    public TaskDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean existsById(Integer taskId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM task WHERE id=?");
            stm.setInt(1, taskId);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Task save(Task task) {
        try {
            if (!existsById(task.getId())) {
                PreparedStatement stm = connection.
                        prepareStatement("INSERT INTO task (title, details, position, status, task_list_id) VALUES (?, ?, ?, ?, ?)");
                stm.setString(1, task.getTitle());
                stm.setString(2, task.getDetails());
                stm.setInt(3, task.getPosition());
                stm.setString(4, task.getStatus().toString());
                stm.setInt(5, task.getTaskListId());
                if (stm.executeUpdate() != 1) {
                    throw new SQLException("Failed to save the task");
                }
            } else {
                PreparedStatement stm = connection.
                        prepareStatement("UPDATE task SET title=?, details=?, position=?, status=?, task_list_id=? WHERE id=?");
                stm.setString(1, task.getTitle());
                stm.setString(2, task.getDetails());
                stm.setInt(3, task.getPosition());
                stm.setString(4, task.getStatus().toString());
                stm.setInt(5, task.getTaskListId());
                stm.setInt(6, task.getId());
                if (stm.executeUpdate() != 1) {
                    throw new SQLException("Failed to update the task");
                }
            }
            return task;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer taskId) {
        try {
            if (!existsById(taskId)){
                throw new DataAccessException("No task found");
            }
            PreparedStatement stm = connection.prepareStatement("DELETE FROM task WHERE id=?");
            stm.setInt(1, taskId);
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to delete the task");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Task> findById(Integer taskId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM task WHERE id=?");
            stm.setInt(1, taskId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return Optional.of(new Task(rst.getInt("id"),
                        rst.getString("title"),
                        rst.getString("details"),
                        rst.getInt("position"),
                        Task.Status.valueOf(rst.getString("status")),
                        rst.getInt("task_list_id")));
            }else{
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Task> findAll() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM task");
            List<Task> tasks = new ArrayList<>();
            while (rst.next()) {
                tasks.add(new Task(rst.getInt("id"),
                        rst.getString("title"),
                        rst.getString("details"),
                        rst.getInt("position"),
                        Task.Status.valueOf(rst.getString("status")),
                        rst.getInt("task_list_id")));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT COUNT(id) AS count FROM user");
            if (rst.next()){
                return rst.getLong("count");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
