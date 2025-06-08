/* ADMIN DELETE BOOK */

package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminDeleteBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("delete-book.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("../login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rs = checkAdmin.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); checkAdmin.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            int bookId = Integer.parseInt(request.getParameter("book_id"));

            // Delete sql statement
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM books WHERE id = ?");
            delete.setInt(1, bookId);
            // Execute in order to update the db
            delete.executeUpdate();


            rs.close(); checkAdmin.close(); delete.close(); conn.close();
            // Go back to admin dashboard
            response.sendRedirect("../admin-dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting book: " + e.getMessage());
        }
    }
}
