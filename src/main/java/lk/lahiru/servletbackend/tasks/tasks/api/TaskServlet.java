package lk.lahiru.servletbackend.tasks.tasks.api;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import lk.ijse.dep8.tasks.dto.TaskDTO;
import lk.ijse.dep8.tasks.util.HttpServlet2;
import lk.ijse.dep8.tasks.util.ResponseStatusException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "TaskServlet")
public class TaskServlet extends HttpServlet2 {

    private final Logger logger = Logger.getLogger(TaskServlet.class.getName());
    private AtomicReference<DataSource> pool;

    @Override
    public void init() {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/pool");
            pool = new AtomicReference<>(ds);
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "Failed to locate the JNDI pool", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
            throw new ResponseStatusException(415, "Invalid content type or content type is empty");
        }

        String pattern = "/([A-Fa-f0-9\\-]{36})/lists/(\\d+)/tasks/?";
        if (!req.getPathInfo().matches(pattern)) {
            throw new ResponseStatusException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid end point for POST request");
        }
        Matcher matcher = Pattern.compile(pattern).matcher(req.getPathInfo());
        matcher.find();
        String userId = matcher.group(1);
        int taskListId = Integer.parseInt(matcher.group(2));

        Connection connection = null;
        try {
            connection = pool.get().getConnection();

            PreparedStatement stm = connection.
                    prepareStatement("SELECT * FROM task_list t WHERE t.id=? AND t.user_id=?");
            stm.setInt(1, taskListId);
            stm.setString(2, userId);
            if (!stm.executeQuery().next()) {
                throw new ResponseStatusException(404, "Invalid user id or task list id");
            }

            Jsonb jsonb = JsonbBuilder.create();
            TaskDTO task = jsonb.fromJson(req.getReader(), TaskDTO.class);

            if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(400, "Invalid title or title is empty");
            }
            task.setPosition(0);
            task.setStatusAsEnum(TaskDTO.Status.NEEDS_ACTION);

            connection.setAutoCommit(false);
            pushDown(connection, 0, taskListId);

            stm = connection.
                    prepareStatement("INSERT INTO task (title, details, position, status, task_list_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, task.getTitle());
            stm.setString(2, task.getNotes());
            stm.setInt(3, task.getPosition());
            stm.setString(4, task.getStatus());
            stm.setInt(5, taskListId);
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to save the task list");
            }

            ResultSet rst = stm.getGeneratedKeys();
            rst.next();
            task.setId(rst.getInt(1));
            connection.commit();

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            jsonb.toJson(task, resp.getWriter());
        } catch (JsonbException e) {
            throw new ResponseStatusException(400, "Invalid JSON", e);
        } catch (SQLException e) {
            throw new ResponseStatusException(500, e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                    connection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void pushDown(Connection connection, int pos, int taskListId) throws SQLException {
        PreparedStatement pstm = connection.
                prepareStatement("UPDATE task t SET position = position + 1 WHERE t.position >= ? AND t.task_list_id = ? ORDER BY t.position");
        pstm.setInt(1, pos);
        pstm.setInt(2, taskListId);
        pstm.executeUpdate();
    }

    private void pushUp(Connection connection, int pos, int taskListId) throws SQLException {
        PreparedStatement pstm = connection.
                prepareStatement("UPDATE task t SET position = position - 1 WHERE t.position >= ? AND t.task_list_id = ? ORDER BY t.position");
        pstm.setInt(1, pos);
        pstm.setInt(2, taskListId);
        pstm.executeUpdate();
    }

    private TaskDTO getTask(HttpServletRequest req) {

        String pattern = "^/([A-Fa-f0-9\\-]{36})/lists/(\\d+)/tasks/(\\d+)/?$";
        if (!req.getPathInfo().matches(pattern)) {
            throw new ResponseStatusException(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    String.format("Invalid end point for %s request", req.getMethod()));
        }
        Matcher matcher = Pattern.compile(pattern).matcher(req.getPathInfo());
        matcher.find();
        String userId = matcher.group(1);
        int taskListId = Integer.parseInt(matcher.group(2));
        int taskId = Integer.parseInt(matcher.group(3));

        try (Connection connection = pool.get().getConnection()) {
            PreparedStatement stm = connection.
                    prepareStatement("SELECT * FROM task_list tl INNER JOIN task t ON t.task_list_id = tl.id WHERE t.id=? AND tl.id=? AND tl.user_id=?");
            stm.setInt(1, taskId);
            stm.setInt(2, taskListId);
            stm.setString(3, userId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String title = rst.getString("title");
                String details = rst.getString("details");
                int position = rst.getInt("position");
                String status = rst.getString("status");
                return new TaskDTO(taskId, title, position, details, status, taskListId);
            } else {
                throw new ResponseStatusException(404, "Invalid user id or task list id or task id");
            }
        } catch (SQLException e) {
            throw new ResponseStatusException(500, "Failed to fetch task details");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TaskDTO task = getTask(req);
        Connection connection = null;
        try {
            connection = pool.get().getConnection();
            connection.setAutoCommit(false);
            pushUp(connection, task.getPosition(), task.getTaskListId());
            PreparedStatement stm = connection.prepareStatement("DELETE FROM task WHERE id=?");
            stm.setInt(1, task.getId());
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to delete the task");
            }
            connection.commit();
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            throw new ResponseStatusException(500, e.getMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pattern = "^/([A-Fa-f0-9\\-]{36})/lists/(\\d+)/tasks/?$";
        Matcher matcher = Pattern.compile(pattern).matcher(req.getPathInfo());
        if (matcher.find()) {
            String userId = matcher.group(1);
            int taskListId = Integer.parseInt(matcher.group(2));

            try (Connection connection = pool.get().getConnection()) {
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM task_list t WHERE t.id=? AND t.user_id=?");
                stm.setInt(1, taskListId);
                stm.setString(2, userId);
                if (!stm.executeQuery().next()) {
                    throw new ResponseStatusException(404, "Invalid task list id");
                }

                stm = connection.prepareStatement("SELECT * FROM task WHERE task.task_list_id = ? ORDER BY position");
                stm.setInt(1, taskListId);
                ResultSet rst = stm.executeQuery();

                List<TaskDTO> tasks = new ArrayList<>();
                while (rst.next()) {
                    int id = rst.getInt("id");
                    String title = rst.getString("title");
                    String details = rst.getString("details");
                    int position = rst.getInt("position");
                    String status = rst.getString("status");
                    tasks.add(new TaskDTO(id, title, position, details, status, taskListId));
                }

                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                String jsonArray = jsonb.toJson(tasks);

                JsonParser parser = Json.createParser(new StringReader(jsonArray));
                parser.next();
                JsonArray tasksArray = parser.getArray();

                JsonObject json = Json.createObjectBuilder().
                        add("resource", Json.createObjectBuilder().add("items", tasksArray)).build();
                resp.getWriter().println(json);
            } catch (SQLException e) {
                throw new ResponseStatusException(500, e.getMessage(), e);
            }

        } else {
            TaskDTO task = getTask(req);
            resp.setContentType("application/json");
            Jsonb jsonb = JsonbBuilder.create();
            jsonb.toJson(task, resp.getWriter());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().startsWith("application/json")) {
            throw new ResponseStatusException(415, "Invalid content type or content type is empty");
        }

        TaskDTO oldTask = getTask(req);
        Connection connection = null;
        try {
            Jsonb jsonb = JsonbBuilder.create();
            TaskDTO newTask = jsonb.fromJson(req.getReader(), TaskDTO.class);

            if (newTask.getTitle() == null || newTask.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(400, "Invalid title or title is empty");
            } else if (newTask.getPosition() == null || newTask.getPosition() < 0) {
                throw new ResponseStatusException(400, "Invalid position or position value is empty");
            }

            connection = pool.get().getConnection();
            connection.setAutoCommit(false);
            if (!oldTask.getPosition().equals(newTask.getPosition())) {
                pushUp(connection, oldTask.getPosition(), oldTask.getTaskListId());
                pushDown(connection, newTask.getPosition(), oldTask.getTaskListId());
            }

            PreparedStatement stm = connection.
                    prepareStatement("UPDATE task SET title=?, details=?, position=?, status=? WHERE id=?");
            stm.setString(1, newTask.getTitle());
            stm.setString(2, newTask.getNotes());
            stm.setInt(3, newTask.getPosition());
            stm.setString(4, newTask.getStatus());
            stm.setInt(5, oldTask.getId());
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to update the task");
            }

            connection.commit();
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (JsonbException e) {
            throw new ResponseStatusException(400, "Invalid JSON");
        } catch (SQLException e) {
            throw new ResponseStatusException(500, e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                    connection.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
