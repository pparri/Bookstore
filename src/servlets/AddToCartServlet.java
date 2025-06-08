/* ADD TO CART */

package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import model.CartItem;

public class AddToCartServlet extends HttpServlet {

    // Override in order to overwrite the default definition
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Opens session with servlet.http library
        HttpSession session = request.getSession(false);

        // Checks wether username exists
        if (session == null || session.getAttribute("username") == null) {
            // If not exists, redirect to login page
            response.sendRedirect("/bookstore/login.html");
            return;
        }

        // Gets username of the session
        String username = (String) session.getAttribute("username");

        try {
            // Connects to the db through jdbc driver
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            // Checks if user is admin, otherwise cannot add to the cart
            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rs = checkAdmin.executeQuery();

            // If it's admin, sends error
            if (!rs.next() || rs.getBoolean("is_admin")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admins cannot use cart.");
                return;
            }

            // Get the general book info
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

            // Get the cart or creating if doesn't exist
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }

            // Add to cart
            cart.add(new CartItem(bookId, title, quantity, price));

            // Close the connection
            conn.close(); bookStmt.close(); bookRs.close(); rs.close(); checkAdmin.close();

            response.sendRedirect("/bookstore/dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error adding to cart: " + e.getMessage());
        }
    }
}

