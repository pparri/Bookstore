package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminListBooksServlet extends HttpServlet {

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
                rsAdmin.close(); checkAdmin.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Book List</title><link rel=\"stylesheet\" type=\"text/css\" href=\"/bookstore/css/style.css\"></head><body>");
            response.getWriter().println("<h2>All Books</h2>");
            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr><th>ID</th><th>Title</th><th>Author</th><th>Price</th><th>Quantity</th><th>Image</th></tr>");

            while (rs.next()) {
                response.getWriter().println("<tr>");
                response.getWriter().println("<td>" + rs.getInt("id") + "</td>");
                response.getWriter().println("<td>" + rs.getString("title") + "</td>");
                response.getWriter().println("<td>" + rs.getString("author") + "</td>");
                response.getWriter().println("<td>" + rs.getDouble("price") + "</td>");
                response.getWriter().println("<td>" + rs.getInt("quantity") + "</td>");
                String image = rs.getString("cover_image");
                if (image != null && !image.isEmpty()) {
                    response.getWriter().println("<td><img src='" + image + "' width='50'></td>");
                } else {
                    response.getWriter().println("<td>—</td>");
                }
                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table><br>");
            response.getWriter().println("<a href='/bookstore/admin-dashboard.html'>Back to Dashboard</a>");
            response.getWriter().println("</body></html>");

            rs.close(); stmt.close(); checkAdmin.close(); rsAdmin.close(); conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
