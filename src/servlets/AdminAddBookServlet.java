package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminAddBookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("../add-book.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

            // Verificar si el usuario es admin
            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rs = checkAdmin.executeQuery();

            if (!rs.next() || !rs.getBoolean("is_admin")) {
                rs.close(); checkAdmin.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            // Obtener parámetros del formulario
            String title = request.getParameter("title");
            String authors = request.getParameter("authors");
            String imageUrl = request.getParameter("image_url");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            // Insertar libro
            PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO books (title, authors, image_url, description, price, quantity) " +
                "VALUES (?, ?, ?, ?, ?, ?)"
            );
            insert.setString(1, title);
            insert.setString(2, authors);
            insert.setString(3, imageUrl);
            insert.setString(4, description);
            insert.setDouble(5, price);
            insert.setInt(6, quantity);
            insert.executeUpdate();

            insert.close(); rs.close(); checkAdmin.close(); conn.close();
            response.sendRedirect("admin-dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding book: " + e.getMessage());
        }
    }
}
