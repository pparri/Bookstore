package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import model.CartItem;

public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rs = checkAdmin.executeQuery();

            if (!rs.next() || rs.getBoolean("is_admin")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins cannot use cart.");
                return;
            }

            int bookId = Integer.parseInt(request.getParameter("book_id"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));

            PreparedStatement bookStmt = conn.prepareStatement(
                    "SELECT title, price FROM books WHERE id = ?");
            bookStmt.setInt(1, bookId);
            ResultSet bookRs = bookStmt.executeQuery();

            if (!bookRs.next()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not found.");
                return;
            }

            String title = bookRs.getString("title");
            double price = bookRs.getDouble("price");

            // Obtener carrito o crearlo si no existe
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            // Añadir al carrito
            cart.add(new CartItem(bookId, title, quantity, price));

            conn.close(); bookStmt.close(); bookRs.close(); rs.close(); checkAdmin.close();

            response.sendRedirect("/bookstore/dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding to cart: " + e.getMessage());
        }
    }
}

