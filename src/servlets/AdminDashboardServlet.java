package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); stmt.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            rs.close(); stmt.close(); conn.close();
            response.sendRedirect("admin-dashboard.html");
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
