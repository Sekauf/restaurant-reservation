package com.restaurant.reservation.ui;

import com.restaurant.reservation.service.TableService;
import com.restaurant.reservation.model.Table;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Einfache Darstellung eines grafischen Tischplans.
 * Die Tisch-Labels lassen sich mit der Maus verschieben (Drag & Drop),
 * die Positionen werden jedoch nicht persistent gespeichert.
 */
public class FloorPlanFrame extends JFrame {
    public FloorPlanFrame() {
        setTitle("Tischplan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600,400);
        setLocationRelativeTo(null);

        JLayeredPane pane = new JLayeredPane();
        pane.setLayout(null);

        TableService tableService = new TableService();
        List<Table> tables;
        try {
            tables = tableService.getAllTables();
        } catch (SQLException e) {
            tables = java.util.Collections.emptyList();
        }

        int x = 20, y = 20;
        for (Table t : tables) {
            JLabel lbl = createDraggableLabel("T" + t.getId());
            lbl.setBounds(x, y, 60, 40);
            pane.add(lbl);
            x += 70;
            if (x > 500) { x = 20; y += 50; }
        }

        add(pane);
    }

    private JLabel createDraggableLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(222,184,135));
        lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        MouseAdapter ma = new MouseAdapter() {
            Point offset;
            @Override
            public void mousePressed(MouseEvent e) {
                offset = e.getPoint();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(lbl, e.getPoint(), lbl.getParent());
                lbl.setLocation(p.x - offset.x, p.y - offset.y);
            }
        };
        lbl.addMouseListener(ma);
        lbl.addMouseMotionListener(ma);
        return lbl;
    }
}
