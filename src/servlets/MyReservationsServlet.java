package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class MyReservationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("/bookstore/login.html");
            return;
        }

        String username = (String) session.getAttribute("username");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement stmt = conn.prepareStatement(
                "SELECT b.title, b.cover_image, r.quantity, r.reservation_date " +
                "FROM reservations r " +
                "JOIN users u ON r.user_id = u.id " +
                "JOIN books b ON r.book_id = b.id " +
                "WHERE u.username = ?"
            );
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>My Reservations</title></head><body>");
            response.getWriter().println("<h2>My Reservations</h2>");

            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr><th>Title</th><th>Cover</th><th>Quantity</th><th>Date</th></tr>");

            while (rs.next()) {
                response.getWriter().println("<tr>");
                response.getWriter().println("<td>" + rs.getString("title") + "</td>");
                String cover = rs.getString("cover_image");
                response.getWriter().println("<td><img src='" + (cover != null ? cover : "#") + "' height='60'></td>");
                response.getWriter().println("<td>" + rs.getInt("quantity") + "</td>");
                response.getWriter().println("<td>" + rs.getTimestamp("reservation_date") + "</td>");
                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table><br>");
            response.getWriter().println("<a href='/bookstore/dashboard.html'>Back</a>");
            response.getWriter().println("</body></html>");

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error loading reservations: " + e.getMessage());
        }
    }
}
