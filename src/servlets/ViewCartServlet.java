package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.CartItem;

public class ViewCartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("/bookstore/login.html");
            return;
        }

        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        response.setContentType("text/html");
        response.getWriter().println("<html><head><title>Your Cart</title></head><body>");
        response.getWriter().println("<h2>Your Cart</h2>");

        if (cart == null || cart.isEmpty()) {
            response.getWriter().println("<p>Your cart is empty.</p>");
        } else {
            double total = 0;
            response.getWriter().println("<form method='POST' action='/bookstore/cart/checkout'>");
            response.getWriter().println("<table border='1'>");
            response.getWriter().println("<tr><th>Title</th><th>Quantity</th><th>Price</th><th>Total</th></tr>");

            for (CartItem item : cart) {
                double subtotal = item.quantity * item.price;
                total += subtotal;

                response.getWriter().println("<tr>");
                response.getWriter().println("<td>" + item.title + "</td>");
                response.getWriter().println("<td>" + item.quantity + "</td>");
                response.getWriter().println("<td>" + item.price + " e</td>");
                response.getWriter().println("<td>" + subtotal + " e</td>");
                response.getWriter().println("</tr>");
            }

            response.getWriter().println("</table>");
            response.getWriter().println("<p><strong>Total: " + total + " </strong></p>");
            response.getWriter().println("<form method='POST' action='/bookstore/cart/checkout'>");
            response.getWriter().println("<button type='submit'>Confirm Reservation</button>");
            response.getWriter().println("</form>");
        }

        response.getWriter().println("<br><a href='/bookstore/dashboard.html'>Back</a>");
        response.getWriter().println("</body></html>");
    }
}

