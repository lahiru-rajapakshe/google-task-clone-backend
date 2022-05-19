package lk.lahiru.servletbackend.tasks.api;

import lk.lahiru.servletbackend.tasks.Util.HttpServlet2;
import lk.lahiru.servletbackend.tasks.Util.ResponseStatusException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;


@WebServlet(name = "TestServlet", value = "/test")
public class TestServlet extends HttpServlet2 {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new ResponseStatusException(201, "Something goes bad");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}