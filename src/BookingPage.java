import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingPage extends JFrame {

    JTable table;
    DefaultTableModel model;
    JComboBox<String> guestBox, roomBox;
    JTextField checkInField, checkOutField, searchField;
    JButton bookBtn, checkoutBtn, clearBtn, homeBtn;
    int selectedId = 0;
    String loggedInUser;

    public BookingPage(String username) {
        this.loggedInUser = username;

        setTitle("Hotel - Booking Management");
        setSize(1050, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1050, 50);
        header.setBackground(new Color(142, 68, 173));
        JLabel title = new JLabel("📅 Booking Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBounds(15, 13, 280, 28);
        header.add(title);
        panel.add(header);

        // Form
        JPanel form = new JPanel(null);
        form.setBounds(10, 60, 320, 340);
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Book Room"));

        addLabel(form, "Select Guest:", 10, 25);
        guestBox = new JComboBox<>();
        guestBox.setBounds(10, 45, 290, 30);
        form.add(guestBox);

        addLabel(form, "Select Room:", 10, 85);
        roomBox = new JComboBox<>();
        roomBox.setBounds(10, 105, 290, 30);
        form.add(roomBox);

        addLabel(form, "Check-In Date (YYYY-MM-DD):", 10, 145);
        checkInField = new JTextField();
        checkInField.setBounds(10, 165, 290, 30);
        form.add(checkInField);

        addLabel(form, "Check-Out Date (YYYY-MM-DD):", 10, 205);
        checkOutField = new JTextField();
        checkOutField.setBounds(10, 225, 290, 30);
        form.add(checkOutField);

        bookBtn = new JButton("BOOK ROOM");
        bookBtn.setBounds(10, 280, 135, 35);
        bookBtn.setBackground(new Color(142, 68, 173));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false); bookBtn.setBorderPainted(false);
        bookBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        form.add(bookBtn);

        checkoutBtn = new JButton("CHECK OUT");
        checkoutBtn.setBounds(155, 280, 135, 35);
        checkoutBtn.setBackground(new Color(192, 57, 43));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false); checkoutBtn.setBorderPainted(false);
        checkoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        form.add(checkoutBtn);

        panel.add(form);

        // Search
        searchField = new JTextField();
        searchField.setBounds(340, 62, 200, 30);
        panel.add(searchField);

        JButton searchBtn = createSmallBtn("Search", new Color(142, 68, 173));
        searchBtn.setBounds(550, 62, 80, 30);
        panel.add(searchBtn);

        clearBtn = createSmallBtn("Clear", new Color(127, 140, 141));
        clearBtn.setBounds(640, 62, 70, 30);
        panel.add(clearBtn);

        homeBtn = createSmallBtn("🏠 Home", new Color(44, 62, 80));
        homeBtn.setBounds(930, 62, 90, 30);
        panel.add(homeBtn);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Guest", "Room", "Check-In", "Check-Out", "Status", "Total"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(142, 68, 173));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(142, 68, 173));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(340, 100, 690, 450);
        panel.add(scroll);

        add(panel);
        loadGuests();
        loadAvailableRooms();
        loadData("");

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
            }
        });

        bookBtn.addActionListener(e -> bookRoom());
        checkoutBtn.addActionListener(e -> checkOut());
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser); dispose(); });

        setVisible(true);
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setBounds(x, y, 250, 20);
        p.add(l);
    }

    JButton createSmallBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    void loadGuests() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT id, name FROM guests");
            while (rs.next()) {
                guestBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void loadAvailableRooms() {
        roomBox.removeAllItems();
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT id, room_no, type, price FROM rooms WHERE status='Available'");
            while (rs.next()) {
                roomBox.addItem(rs.getInt("id") + " - Room " + rs.getString("room_no") +
                    " (" + rs.getString("type") + ") ₹" + rs.getDouble("price"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void bookRoom() {
        if (guestBox.getItemCount() == 0 || roomBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No guests or available rooms!"); return;
        }

        String checkIn = checkInField.getText().trim();
        String checkOut = checkOutField.getText().trim();

        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all dates!"); return;
        }

        try {
            String guestSelected = guestBox.getSelectedItem().toString();
            int guestId = Integer.parseInt(guestSelected.split(" - ")[0]);

            String roomSelected = roomBox.getSelectedItem().toString();
            int roomId = Integer.parseInt(roomSelected.split(" - ")[0]);

            // Get room price
            PreparedStatement rps = DBConnection.getConnection().prepareStatement("SELECT price FROM rooms WHERE id=?");
            rps.setInt(1, roomId);
            ResultSet rrs = rps.executeQuery();
            double pricePerNight = 0;
            if (rrs.next()) pricePerNight = rrs.getDouble("price");

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO bookings (guest_id, room_id, check_in, check_out, status, total) VALUES (?,?,?,?,?,?)");
            ps.setInt(1, guestId); ps.setInt(2, roomId);
            ps.setString(3, checkIn); ps.setString(4, checkOut);
            ps.setString(5, "Booked"); ps.setDouble(6, pricePerNight);
            ps.executeUpdate();

            // Update room status
            PreparedStatement ups = DBConnection.getConnection().prepareStatement(
                "UPDATE rooms SET status='Booked' WHERE id=?");
            ups.setInt(1, roomId); ups.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room Booked Successfully!");
            loadData(""); loadAvailableRooms(); clearForm();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void checkOut() {
        if (selectedId == 0) { JOptionPane.showMessageDialog(this, "Select a booking first!"); return; }
        int c = JOptionPane.showConfirmDialog(this, "Check out this guest?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == 0) {
            try {
                // Get room id from booking
                PreparedStatement gps = DBConnection.getConnection().prepareStatement("SELECT room_id FROM bookings WHERE id=?");
                gps.setInt(1, selectedId);
                ResultSet grs = gps.executeQuery();
                if (grs.next()) {
                    int roomId = grs.getInt("room_id");
                    // Update room to available
                    PreparedStatement ups = DBConnection.getConnection().prepareStatement("UPDATE rooms SET status='Available' WHERE id=?");
                    ups.setInt(1, roomId); ups.executeUpdate();
                }

                PreparedStatement ps = DBConnection.getConnection().prepareStatement("UPDATE bookings SET status='Checked Out' WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Guest Checked Out!");
                loadData(""); loadAvailableRooms();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    void loadData(String search) {
        model.setRowCount(0);
        try {
            PreparedStatement ps;
            if (search.isEmpty()) {
                ps = DBConnection.getConnection().prepareStatement(
                    "SELECT b.id, g.name, r.room_no, b.check_in, b.check_out, b.status, b.total " +
                    "FROM bookings b JOIN guests g ON b.guest_id=g.id JOIN rooms r ON b.room_id=r.id ORDER BY b.id DESC");
            } else {
                ps = DBConnection.getConnection().prepareStatement(
                    "SELECT b.id, g.name, r.room_no, b.check_in, b.check_out, b.status, b.total " +
                    "FROM bookings b JOIN guests g ON b.guest_id=g.id JOIN rooms r ON b.room_id=r.id " +
                    "WHERE g.name LIKE ? OR r.room_no LIKE ?");
                ps.setString(1, "%" + search + "%"); ps.setString(2, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), "₹" + rs.getDouble(7)
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

   void clearForm() {

    JOptionPane.showMessageDialog(this, "Clear Button Clicked");

    guestBox.setSelectedIndex(0);
    roomBox.setSelectedIndex(0);
    checkInField.setText("");
    checkOutField.setText("");
    searchField.setText("");
    table.clearSelection();
    selectedId = 0;
    loadData("");
}
}