import com.restaurant.reservation.dao.ReservationDAO;
import com.restaurant.reservation.model.Reservation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationDAOTest {
    private ReservationDAO dao;

    @BeforeEach
    public void setup() {
        ReservationDAO.createTable();
        dao = new ReservationDAO();
    }

    @AfterEach
    public void cleanup() throws Exception {
        // delete all reservations to keep DB clean
        List<Reservation> all = dao.getAllReservations();
        for (Reservation r : all) {
            dao.deleteReservation(r.getId());
        }
        // also remove entries from cancellations table
        try (java.sql.Connection conn = com.restaurant.reservation.dao.Database.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM cancellations");
        }
    }

    @Test
    public void testAddAndDeleteReservation() throws Exception {
        int before = dao.getAllReservations().size();
        Reservation r = new Reservation("Test", "2025-01-01", "12:00", 2, 1);
        dao.addReservation(r);
        List<Reservation> list = dao.getAllReservations();
        assertEquals(before + 1, list.size());
        Reservation added = list.stream()
                .filter(x -> x.getName().equals("Test") && x.getDate().equals("2025-01-01"))
                .findFirst()
                .orElseThrow();
        dao.deleteReservation(added.getId());
        assertEquals(before, dao.getAllReservations().size());
    }
}
