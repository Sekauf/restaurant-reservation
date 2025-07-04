package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.ReservationService;
import com.restaurant.reservation.service.TableService;
import com.restaurant.reservation.model.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog für die Auswahl eines freien Tisches.
 */
public class TableSelectionDialog extends JDialog {
    private final ReservationService reservationService;
    private JTable table;
    private DefaultTableModel model;
    private Integer selectedTable;
    private final String date;
    private final String time;
    private final int persons;
    private final boolean projector;


    public TableSelectionDialog(Window owner, ReservationService service, String date, String time, int persons, boolean projector) {
        super(owner, "Tisch auswählen", ModalityType.APPLICATION_MODAL);
        this.reservationService = service;
        this.date = date;
        this.time = time;
        this.persons = persons;
        this.projector = projector;
        buildUi();
        loadData();
    }

    private void buildUi() {
        setLayout(new BorderLayout(5,5));
        ((JComponent)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        model = new DefaultTableModel(new Object[]{"Tisch","Sitzplätze","Status"},0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl,val,selected,focus,row,col);
                Boolean res = (Boolean) tbl.getModel().getValueAt(row,2);
                if (Boolean.TRUE.equals(res)) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton chooseBtn = new JButton("Auswählen");
        add(chooseBtn, BorderLayout.SOUTH);

        chooseBtn.addActionListener(e -> onChoose());

        setSize(400,300);
        setLocationRelativeTo(getOwner());
    }

    private void loadData() {
        TableService tableService = new TableService();
        List<Table> tables;
        try {
            tables = tableService.getAllTables();
        } catch (Exception ex) {
            tables = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Tische.", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
        }

        model.setRowCount(0);
        for (Table t : tables) {
            if (t.getSeats() < persons) continue;
            if (projector && !t.isHasProjector()) continue;
            boolean reserved = false;
            try {
                reserved = reservationService.isTableReserved(date, time, t.getId());
            } catch (Exception e) {
                reserved = false;
            }
            model.addRow(new Object[]{t.getId(), t.getSeats(), reserved});
        }
    }

    private void onChoose() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Bitte einen Tisch auswählen.");
            return;
        }
        boolean reserved = (Boolean) model.getValueAt(row,2);
        if (reserved) {
            JOptionPane.showMessageDialog(this, "Dieser Tisch ist bereits reserviert.");
            return;
        }
        selectedTable = (Integer) model.getValueAt(row,0);
        dispose();
    }

    public Integer getSelectedTable() {
        return selectedTable;
    }
}
