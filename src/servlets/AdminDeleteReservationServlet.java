/* ADMIN DELETE RESERVATION */

package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class AdminDeleteReservationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

            // Check admin
            PreparedStatement checkAdmin = conn.prepareStatement(
                    "SELECT is_admin FROM users WHERE username = ?");
            checkAdmin.setString(1, username);
            ResultSet rsAdmin = checkAdmin.executeQuery();

            if (!rsAdmin.next() || !rsAdmin.getBoolean("is_admin")) {
                rsAdmin.close(); checkAdmin.close(); conn.close();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied.");
                return;
            }

            int reservationId = Integer.parseInt(request.getParameter("reservation_id"));

            // Delete reservation
            PreparedStatement delete = conn.prepareStatement(
                    "DELETE FROM reservations WHERE id = ?");
            delete.setInt(1, reservationId);
            int rows = delete.executeUpdate();

            System.out.println("Reserva eliminada (ID=" + reservationId + "), filas afectadas: " + rows);

            delete.close(); rsAdmin.close(); checkAdmin.close(); conn.close();

            response.sendRedirect("/bookstore/admin-dashboard/view-reservations");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting reservation: " + e.getMessage());
        }
    }
}
