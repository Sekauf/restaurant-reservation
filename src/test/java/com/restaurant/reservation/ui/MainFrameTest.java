import com.restaurant.reservation.model.Reservation;
import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.ui.MainFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple UI test for the MainFrame using an in-memory ReservationService.
 */
public class MainFrameTest {

    static class InMemoryService extends ReservationService {
        private final List<Reservation> list = new ArrayList<>();
        private int nextId = 1;

        @Override
        public List<Reservation> getAllReservations() {
            return new ArrayList<>(list);
        }

        @Override
        public void addReservation(String name, String date, String time, int persons, int tableNumber) {
            Reservation r = new Reservation(nextId++, name, date, time, persons, tableNumber);
            list.add(r);
        }

        @Override
        public void deleteReservation(int reservationId) {
            list.removeIf(r -> r.getId() == reservationId);
        }
    }

    private MainFrame frame;
    private InMemoryService service;

    @BeforeEach
    public void setup() {
        System.setProperty("java.awt.headless", "true");
        service = new InMemoryService();
        frame = new MainFrame(service);
    }

    @AfterEach
    public void tearDown() {
        if (frame != null) {
            frame.dispose();
        }
    }

    @Test
    public void testAddReservationViaUI() {
        JTextField nameField = (JTextField) TestUtils.getField(frame, "nameField");
        JComboBox<?> dateCombo = (JComboBox<?>) TestUtils.getField(frame, "dateCombo");
        JComboBox<?> timeCombo = (JComboBox<?>) TestUtils.getField(frame, "timeCombo");
        JComboBox<?> personsCombo = (JComboBox<?>) TestUtils.getField(frame, "personsCombo");
        JComboBox<?> tableNumberCombo = (JComboBox<?>) TestUtils.getField(frame, "tableNumberCombo");
        JButton addButton = (JButton) TestUtils.getField(frame, "addButton");

        nameField.setText("Testuser");
        dateCombo.setSelectedIndex(0);
        timeCombo.setSelectedIndex(0);
        personsCombo.setSelectedItem(2);
        tableNumberCombo.setSelectedItem(3);

        addButton.doClick();

        assertEquals(1, service.getAllReservations().size());
        Reservation r = service.getAllReservations().get(0);
        assertEquals("Testuser", r.getName());
    }
}
