/* ADMIN ADD BOOK */

package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminAddBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("add-book.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Checks wether username exists
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

            // Checks if user is admin, otherwise cannot continue
            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rs = checkAdmin.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); checkAdmin.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            // Get parameters from the form
            String title = request.getParameter("title");
            String authors = request.getParameter("authors");
            String cover_image = request.getParameter("image_url");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Add the book
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO books (title, author, description, price, quantity, cover_image) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            insert.setString(1, title);
            insert.setString(2, authors);
            insert.setString(3, description);
            insert.setDouble(4, price);
            insert.setInt(5, quantity);
            insert.setString(6, cover_image);

            response.setStatus(HttpServletResponse.SC_OK);
            System.out.println("📘 POST recibido: añadiendo libro");
            insert.executeUpdate();
            insert.close(); rs.close(); checkAdmin.close(); conn.close();
            response.sendRedirect("/bookstore/admin-dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding book: " + e.getMessage());
        }
    }
}
