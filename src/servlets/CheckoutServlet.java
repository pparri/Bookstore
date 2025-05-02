package servlets;

import model.CartItem;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import model.CartItem;

public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("/bookstore/view-cart");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión y usuario autenticado
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String username = (String) session.getAttribute("username");
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.getWriter().println("Cart is empty.");
            return;
        }

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/bookstore", "mysql", "mysql");

            // Obtener ID del usuario
            PreparedStatement getUserId = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            getUserId.setString(1, username);
            ResultSet rsUser = getUserId.executeQuery();

            if (!rsUser.next()) {
                rsUser.close();
                getUserId.close();
                response.getWriter().println("User not found.");
                return;
            }

            int userId = rsUser.getInt("id");
            rsUser.close();
            getUserId.close();

            // Insertar reservas y actualizar stock
            for (CartItem item : cart) {

                PreparedStatement checkStock = conn.prepareStatement("SELECT quantity FROM books WHERE id = ?");
                checkStock.setInt(1, item.getBookId());
                ResultSet stockRes = checkStock.executeQuery();

                if (stockRes.next()) {
                    int available = stockRes.getInt("quantity");
                    if (available < item.getQuantity()) {
                        response.getWriter().println("Not enough stock for book: " + item.getTitle());
                        return;
                    }
                }
                stockRes.close();
                checkStock.close();

                PreparedStatement insertRes = conn.prepareStatement(
                    "INSERT INTO reservations (user_id, book_id, quantity) VALUES (?, ?, ?)"
                );
                insertRes.setInt(1, userId);
                insertRes.setInt(2, item.getBookId());
                insertRes.setInt(3, item.getQuantity());
                insertRes.executeUpdate();
                insertRes.close();

                PreparedStatement updateStock = conn.prepareStatement(
                    "UPDATE books SET quantity = quantity - ? WHERE id = ?"
                );
                updateStock.setInt(1, item.getQuantity());
                updateStock.setInt(2, item.getBookId());
                updateStock.executeUpdate();
                updateStock.close();
            }

            // Vaciar el carrito
            session.removeAttribute("cart");

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Checkout Complete</title></head><body>");
            response.getWriter().println("<h2>Your reservation was successful!</h2>");
            response.getWriter().println("<a href='/bookstore/dashboard.html'>Back to Dashboard</a>");
            response.getWriter().println("</body></html>");

            conn.close();
            response.sendRedirect("bookstore/dashboard.html");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error during checkout: " + e.getMessage());
        }
    }
}
