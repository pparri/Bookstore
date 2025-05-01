package servlets;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Quitar el.html
        response.sendRedirect("register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String isAdminParam = request.getParameter("is_admin");
        boolean isAdmin = isAdminParam != null;

        String dbUrl = "jdbc:mariadb://localhost:3306/bookstore";
        String dbUser = "mysql";
        String dbPassword = "mysql";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password, email, is_admin) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, username);
            stmt.setString(2, password); // luego hay que cifrarlo
            stmt.setString(3, email);
            stmt.setBoolean(4, isAdmin);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            // Crear sesión tras registro
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            response.sendRedirect("dashboard.html"); // o dashboard

        } /*catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Registration failed");
        }*/
        
        catch (Exception e) {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("Registration failed:");
            e.printStackTrace(out); // Mostrar todo el stacktrace en la web
        }
    }
}
