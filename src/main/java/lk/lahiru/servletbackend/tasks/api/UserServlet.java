package lk.lahiru.servletbackend.tasks.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep8.tasks.dto.UserDTO;
import lk.ijse.dep8.tasks.service.ServiceFactory;
import lk.ijse.dep8.tasks.service.custom.UserService;
import lk.ijse.dep8.tasks.util.HttpServlet2;
import lk.ijse.dep8.tasks.util.ResponseStatusException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(name = "UserServlet")
public class UserServlet extends HttpServlet2 {

    private final Logger logger = Logger.getLogger(UserServlet.class.getName());

    private UserDTO getUser(HttpServletRequest req) {
        if (!(req.getPathInfo() != null &&
                (req.getPathInfo().replaceAll("/", "").length() == 36))) {
            throw new ResponseStatusException(404, "Invalid user id");
        }

        String userId = req.getPathInfo().replaceAll("/", "");

        try  {
            UserService userService = ServiceFactory.getInstance().
                    getService(ServiceFactory.ServiceTypes.USER);
            if (!userService.existsUser(userId)) {
                throw new ResponseStatusException(404, "Invalid user id");
            } else {
                return userService.getUser(userId);
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Throwable e) {
            throw new ResponseStatusException(500, "Failed to fetch the user info", e);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
            throw new ResponseStatusException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid content type or no content type is provided");
        }

        UserDTO user = getUser(request);

        String name = request.getParameter("name");
        String password = request.getParameter("password");
        Part picture = request.getPart("picture");

        if (name == null || !name.matches("[A-Za-z ]+")) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Invalid name or name is empty");
        } else if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Password can't be empty");
        } else if (picture != null && (picture.getSize() == 0 || !picture.getContentType().startsWith("image"))) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Invalid picture");
        }

        try {

            String pictureUrl = null;
            if (picture != null) {
                pictureUrl = request.getScheme() + "://" + request.getServerName() + ":"
                        + request.getServerPort() + request.getContextPath();
                pictureUrl += "/uploads/" + user.getId();
            }

            UserService userService = ServiceFactory.getInstance().getService(ServiceFactory.ServiceTypes.USER);
            userService.updateUser(new UserDTO(user.getId(), name, user.getEmail(), password, pictureUrl),
                    picture, getServletContext().getRealPath("/"));

            resp.setStatus(204);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Throwable e) {
            throw new ResponseStatusException(500, e.getMessage(), e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO user = getUser(req);
        try  {
            UserService userService = ServiceFactory.getInstance().getService(ServiceFactory.ServiceTypes.USER);
            userService.deleteUser(user.getId(),
                    getServletContext().getRealPath("/"));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (Throwable e) {
            throw new ResponseStatusException(500, e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserDTO user = getUser(req);
        Jsonb jsonb = JsonbBuilder.create();
        resp.setContentType("application/json");
        jsonb.toJson(user, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getContentType() == null || !request.getContentType().startsWith("multipart/form-data")) {
            throw new ResponseStatusException(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid content type or no content type is provided");
        }

        if (request.getPathInfo() != null && !request.getPathInfo().equals("/")) {
            throw new ResponseStatusException(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Invalid end point for a POST request");
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        Part picture = request.getPart("picture");

        if (name == null || !name.matches("[A-Za-z ]+")) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Invalid name or name is empty");
        } else if (email == null || !email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Invalid email or email is empty");
        } else if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Password can't be empty");
        } else if (picture != null && (picture.getSize() == 0 || !picture.getContentType().startsWith("image"))) {
            throw new ResponseStatusException(HttpServletResponse.SC_BAD_REQUEST, "Invalid picture");
        }

        try {
            UserService userService = ServiceFactory.getInstance().getService(ServiceFactory.ServiceTypes.USER);
            if (userService.existsUser(email)) {
                throw new ResponseStatusException(HttpServletResponse.SC_CONFLICT, "A user has been already registered with this email");
            }

            String pictureUrl = null;
            if (picture != null) {
                pictureUrl = request.getScheme() + "://" + request.getServerName() + ":"
                        + request.getServerPort() + request.getContextPath() + "/uploads/";
            }
            UserDTO user = new UserDTO(null, name, email, password, pictureUrl);

            user = userService.registerUser(picture,
                    getServletContext().getRealPath("/"), user);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            Jsonb jsonb = JsonbBuilder.create();
            jsonb.toJson(user, response.getWriter());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Throwable e) {
            throw new ResponseStatusException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to register the user", e);
        }
    }


}
