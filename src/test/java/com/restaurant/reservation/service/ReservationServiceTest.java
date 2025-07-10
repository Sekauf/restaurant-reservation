import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ReservationService using the real DAO and database.
 */
public class ReservationServiceTest {
    private ReservationService service;

    @BeforeEach
    public void setUp() {
        com.restaurant.reservation.dao.ReservationDAO.createTable();
        service = new ReservationService();
    }

    @Test
    public void testAddAndDeleteReservationThroughService() throws Exception {
        int before = service.getAllReservations().size();
        service.addReservation("Alice", "2099-12-12", "18:00", 2, 5);
        List<Reservation> list = service.getAllReservations();
        assertEquals(before + 1, list.size());

        Reservation added = list.stream()
                .filter(r -> r.getName().equals("Alice") && r.getDate().equals("2099-12-12"))
                .findFirst()
                .orElseThrow();
        service.deleteReservation(added.getId());
        assertEquals(before, service.getAllReservations().size());
    }

    @Test
    public void testPreventDoubleBooking() throws Exception {
        service.addReservation("Bob", "2099-01-01", "12:00", 2, 1);
        Exception ex = assertThrows(Exception.class, () ->
                service.addReservation("Carl", "2099-01-01", "12:00", 4, 1));
        assertTrue(ex.getMessage().toLowerCase().contains("bereit"));
    }
}
