package lk.lahiru.servletbackend.tasks.Util;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServlet2 extends HttpServlet {

    private final Logger logger = Logger.getLogger(HttpServlet2.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (Throwable t) {
            logger.logp(Level.SEVERE, t.getStackTrace()[0].getClassName(),
                    t.getStackTrace()[0].getMethodName(), t.getMessage(), t);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);

            resp.setContentType("application/json");
            HttpResponseErrorMsg errorMsg = new HttpResponseErrorMsg(new Date().getTime(),
                    500, "Internal Server Error",
                    sw.toString(), t.getMessage(), req.getRequestURI());
            Jsonb jsonb = JsonbBuilder.create();
            jsonb.toJson(errorMsg, resp.getWriter());
        }
    }
}