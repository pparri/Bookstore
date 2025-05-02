package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class BookDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing book ID.");
            return;
        }

        try {
            int bookId = Integer.parseInt(idParam);

            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement bookStmt = conn.prepareStatement(
                "SELECT * FROM books WHERE id = ?");
            bookStmt.setInt(1, bookId);
            ResultSet rs = bookStmt.executeQuery();

            if (!rs.next()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found.");
                return;
            }

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Book Details</title><link rel=\"stylesheet\" type=\"text/css\" href=\"/bookstore/css/style.css\"></head><body>");
            response.getWriter().println("<h2>" + rs.getString("title") + "</h2>");
            response.getWriter().println("<p><strong>Author:</strong> " + rs.getString("author") + "</p>");
            response.getWriter().println("<p><strong>Description:</strong> " + rs.getString("description") + "</p>");
            response.getWriter().println("<p><strong>Price:</strong> " + rs.getDouble("price") + " e</p>");

            String image = rs.getString("cover_image");
            if (image != null && !image.isEmpty()) {
                response.getWriter().println("<img src='" + image + "' width='150'><br>");
            }

            // Si hay usuario logueado y no es admin
            if (username != null) {
                PreparedStatement isAdminStmt = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
                isAdminStmt.setString(1, username);
                ResultSet rsUser = isAdminStmt.executeQuery();

                if (rsUser.next() && !rsUser.getBoolean("is_admin")) {
                    response.getWriter().println("<form method='POST' action='/bookstore/cart/add'>");
                    response.getWriter().println("<input type='hidden' name='book_id' value='" + bookId + "'>");
                    response.getWriter().println("<label>Quantity: </label>");
                    response.getWriter().println("<input type='number' name='quantity' min='1' value='1'>");
                    response.getWriter().println("<button type='submit'>Add to Cart</button>");
                    response.getWriter().println("</form>");
                }

                rsUser.close(); isAdminStmt.close();
            }

            response.getWriter().println("<br><a href='/bookstore/dashboard.html'>Back</a>");
            response.getWriter().println("</body></html>");

            rs.close(); bookStmt.close(); conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
        }
    }
}
