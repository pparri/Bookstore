package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminListReservationsServlet extends HttpServlet {
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
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rsAdmin = checkAdmin.executeQuery();

            if (!rsAdmin.next() || !rsAdmin.getBoolean("is_admin")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT reservations.id, users.username, books.title, reservations.reservation_date " +
                "FROM reservations " +
                "JOIN users ON reservations.user_id = users.id " +
                "JOIN books ON reservations.book_id = books.id"
            );

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Reservations</title></head><body>");
            response.getWriter().println("<h2>User Reservations</h2>");
            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr><th>ID</th><th>User</th><th>Book</th><th>Date</th><th>Action</th></tr>");

            while (rs.next()) {
                int resId = rs.getInt("id");
                response.getWriter().println("<tr>");
                response.getWriter().println("<td>" + resId + "</td>");
                response.getWriter().println("<td>" + rs.getString("username") + "</td>");
                response.getWriter().println("<td>" + rs.getString("title") + "</td>");
                response.getWriter().println("<td>" + rs.getTimestamp("reservation_date") + "</td>");
                response.getWriter().println("<td><form method='POST' action='/bookstore/admin-dashboard/delete-reservation'>" +
                    "<input type='hidden' name='reservation_id' value='" + resId + "'>" +
                    "<button type='submit'>Delete</button></form></td>");
                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table><br>");
            response.getWriter().println("<a href='/bookstore/admin-dashboard.html'>Back</a>");
            response.getWriter().println("</body></html>");

            rs.close(); stmt.close(); checkAdmin.close(); rsAdmin.close(); conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error loading reservations: " + e.getMessage());
        }
    }
}
