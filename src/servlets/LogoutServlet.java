package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false); // no crea si no existe
        if (session != null) {
            session.invalidate(); // destruye sesión
        }

        response.sendRedirect("login.html");
    }
}
