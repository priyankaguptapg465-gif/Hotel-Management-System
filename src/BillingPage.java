import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingPage extends JFrame {

    JTable table;
    DefaultTableModel model;
    String loggedInUser;

    public BillingPage(String username) {
        this.loggedInUser = username;

        setTitle("Hotel - Billing");
        setSize(950, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel header = new JPanel(null);
        header.setPreferredSize(new Dimension(950, 50));
        header.setBackground(new Color(230, 126, 34));

        JLabel title = new JLabel("💰 Billing & Payments");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBounds(15, 13, 280, 28);
        header.add(title);

        JButton homeBtn = new JButton("🏠 Home");
        homeBtn.setBounds(830, 12, 90, 28);
        homeBtn.setBackground(new Color(44, 62, 80));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setFocusPainted(false); homeBtn.setBorderPainted(false);
        homeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        header.add(homeBtn);

        panel.add(header, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"Booking ID", "Guest", "Room", "Check-In", "Check-Out", "Status", "Amount (₹)", "Action"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(230, 126, 34));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(230, 126, 34));
        table.setSelectionForeground(Color.WHITE);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom - Total
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(236, 240, 241));

        JButton generateBtn = new JButton("🧾 Generate Bill");
        generateBtn.setBackground(new Color(230, 126, 34));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFont(new Font("Arial", Font.BOLD, 14));
        generateBtn.setFocusPainted(false); generateBtn.setBorderPainted(false);
        generateBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateBtn.setPreferredSize(new Dimension(160, 38));
        bottomPanel.add(generateBtn);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel);

        loadData();

        homeBtn.addActionListener(e -> { new HomePage(loggedInUser); dispose(); });

        generateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Select a booking first!"); return; }

            String guest = model.getValueAt(row, 1).toString();
            String room = model.getValueAt(row, 2).toString();
            String checkIn = model.getValueAt(row, 3).toString();
            String checkOut = model.getValueAt(row, 4).toString();
            String amount = model.getValueAt(row, 6).toString();

            JOptionPane.showMessageDialog(this,
                "========== HOTEL BILL ==========\n" +
                "Guest Name  : " + guest + "\n" +
                "Room Number : " + room + "\n" +
                "Check-In    : " + checkIn + "\n" +
                "Check-Out   : " + checkOut + "\n" +
                "================================\n" +
                "Total Amount: " + amount + "\n" +
                "================================\n" +
                "     Thank You! Visit Again!",
                "Bill Receipt", JOptionPane.INFORMATION_MESSAGE);
        });

        setVisible(true);
    }

    void loadData() {
        model.setRowCount(0);
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "SELECT b.id, g.name, r.room_no, b.check_in, b.check_out, b.status, b.total " +
                "FROM bookings b JOIN guests g ON b.guest_id=g.id JOIN rooms r ON b.room_id=r.id ORDER BY b.id DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getDouble(7), "Generate Bill"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}