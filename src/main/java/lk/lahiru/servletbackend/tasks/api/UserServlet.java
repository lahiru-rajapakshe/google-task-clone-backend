package lk.lahiru.servletbackend.tasks.api;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet(name = "UserServlet", value = "/UserServlet")
public class UserServlet extends HttpServlet {

    private final Logger logger= Logger.getLogger(UserServlet.class.getName());

    @Resource(name = "java:comp/env/jdbc/pool")
    private  volatile DataSource pool;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getContentType()==null||request.getContentType().startsWith("multipart/form-data")){
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,"Invalid content type or no content type is provided");
            return;
        }
    }
}
