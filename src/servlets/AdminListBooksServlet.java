package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
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
            Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            PreparedStatement stmt = conn.prepareStatement("SELECT is_admin FROM users WHERE username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); stmt.close(); conn.close();
                response.sendRedirect("../dashboard.html");
                return;
            }

            Statement bookStmt = conn.createStatement();
            ResultSet books = bookStmt.executeQuery("SELECT * FROM books");

            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body><h2>Book List</h2><table border='1'>");
            out.println("<tr><th>ID</th><th>Title</th><th>Authors</th><th>Image</th><th>Price</th><th>Qty</th><th>Actions</th></tr>");

            while (books.next()) {
                int id = books.getInt("id");
                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + books.getString("title") + "</td>");
                out.println("<td>" + books.getString("authors") + "</td>");
                out.println("<td><img src='" + books.getString("image_url") + "' width='80'/></td>");
                out.println("<td>" + books.getDouble("price") + "</td>");
                out.println("<td>" + books.getInt("quantity") + "</td>");
                out.println("<td><a href='delete-book?id=" + id + "'>❌ Delete</a></td>");
                out.println("</tr>");
            }

            out.println("</table><br><a href='../main.html'>⬅️ Back</a></body></html>");

            books.close(); bookStmt.close(); rs.close(); stmt.close(); conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

