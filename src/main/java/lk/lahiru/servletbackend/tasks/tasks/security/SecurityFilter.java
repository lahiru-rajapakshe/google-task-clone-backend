package lk.lahiru.servletbackend.tasks.tasks.security;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep8.tasks.dto.UserDTO;
import lk.ijse.dep8.tasks.util.HttpResponseErrorMsg;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebFilter(filterName = "SecurityFilter", urlPatterns = "/*")
public class SecurityFilter extends HttpFilter {

    @Resource(name="java:comp/env/jdbc/pool")
    private volatile DataSource pool;
    private Logger logger = Logger.getLogger(SecurityFilter.class.getName());

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        /* Excludes from security filter */
        String appContextPath = req.getContextPath();
        if (req.getRequestURI().matches(appContextPath + "/v1/users/?") &&
                req.getMethod().equals("POST")) {
            chain.doFilter(req, res);
            return;
        }

        String authorization = req.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Basic")) {
            sendErrorResponse(req, res);
            return;
        }

        /* Authorization: Basic 123456A4DF1E12D */
        String base64Credentials = authorization.replaceFirst("Basic ", "");
        byte[] decodedByteArray = Base64.getDecoder().decode(base64Credentials);
        String userCredentials = new String(decodedByteArray);

        /* admin:hello:ijse:test */
        String[] split = userCredentials.split(":", 2);
        String username = split[0];
        String password = split[1];

        try (Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM user WHERE email=?");
            stm.setString(1, username);
            ResultSet rst = stm.executeQuery();

            if (!rst.next()){
                sendErrorResponse(req, res);
                return;
            }

            if (!DigestUtils.sha256Hex(password).equals(rst.getString("password"))){
                sendErrorResponse(req, res);
                return;
            }

            SecurityContextHolder.setPrincipal(new UserDTO(rst.getString("id"),
                    rst.getString("full_name"),
                    rst.getString("email"),
                    rst.getString("password"),
                    rst.getString("profile_pic")));

            chain.doFilter(req, res);

        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void sendErrorResponse(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setStatus(401);
        Jsonb jsonb = JsonbBuilder.create();
        jsonb.toJson(new HttpResponseErrorMsg(new Date().getTime(), 401, null,
                "Permission denied", req.getRequestURI()), res.getWriter());
    }
}
