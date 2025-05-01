package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class AdminDeleteBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("../login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        try {
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");
            PreparedStatement stmt = conn.prepareStatement("SELECT is_admin FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); stmt.close(); conn.close();
                response.sendRedirect("../dashboard.html");
                return;
            }

            int id = Integer.parseInt(request.getParameter("id"));
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM books WHERE id = ?");
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();

            deleteStmt.close(); rs.close(); stmt.close(); conn.close();
            response.sendRedirect("list-books");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting book: " + e.getMessage());
        }
    }
}
