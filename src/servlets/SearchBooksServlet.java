package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class SearchBooksServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String query = request.getParameter("query");

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM books WHERE quantity > 0 AND (title LIKE ? OR author LIKE ?)"
            );
            String likeQuery = "%" + query + "%";
            ps.setString(1, likeQuery);
            ps.setString(2, likeQuery);
            ResultSet rs = ps.executeQuery();

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Search Results</title></head><body>");
            response.getWriter().println("<h2>🔍 Search Results for \"" + query + "\"</h2>");

            if (!rs.isBeforeFirst()) {
                response.getWriter().println("<p>No books found.</p>");
            } else {
                response.getWriter().println("<table border='1'>");
                response.getWriter().println("<tr><th>Title</th><th>Author</th><th>Price</th><th>Image</th></tr>");

                while (rs.next()) {
                    int bookId = rs.getInt("id");
                    response.getWriter().println("<tr>");
                    response.getWriter().println("<td>" + rs.getString("title") + "</td>");
                    response.getWriter().println("<td>" + rs.getString("author") + "</td>");
                    response.getWriter().println("<td>" + rs.getDouble("price") + " e</td>");
                    String image = rs.getString("cover_image");
                    if (image != null && !image.isEmpty()) {
                        response.getWriter().println("<td><img src='" + image + "' width='50'></td>");
                    } else {
                        response.getWriter().println("<td>—</td>");
                    }
                    response.getWriter().println("</tr>");
                    response.getWriter().println("<td><a href='/bookstore/dashboard/detailed-book?id=" + bookId + "'>View detailes</a></td>");
                }
                response.getWriter().println("</table>");
            }
            response.getWriter().println("<br><a href='../dashboard.html'>Back to Dashboard</a>");
            response.getWriter().println("</body></html>");

            rs.close(); ps.close(); conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error searching books: " + e.getMessage());
        }
    }
}

