import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class RoomPage extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField roomNoField, priceField, searchField;
    JComboBox<String> typeBox, statusBox;
    JButton addBtn, updateBtn, deleteBtn, clearBtn, homeBtn;
    int selectedId = 0;
    String loggedInUser;

    public RoomPage(String username) {
        this.loggedInUser = username;

        setTitle("Hotel - Room Management");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1000, 50);
        header.setBackground(new Color(41, 128, 185));
        JLabel title = new JLabel("🏠 Room Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBounds(15, 13, 280, 28);
        header.add(title);
        panel.add(header);

        // Form
        JPanel form = new JPanel(null);
        form.setBounds(10, 60, 310, 330);
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Room Details"));

        addLabel(form, "Room Number:", 10, 25);
        roomNoField = new JTextField();
        roomNoField.setBounds(10, 45, 280, 30);
        form.add(roomNoField);

        addLabel(form, "Room Type:", 10, 85);
        typeBox = new JComboBox<>(new String[]{"Single", "Double", "Suite", "Deluxe", "Family"});
        typeBox.setBounds(10, 105, 280, 30);
        form.add(typeBox);

        addLabel(form, "Price per Night (₹):", 10, 145);
        priceField = new JTextField();
        priceField.setBounds(10, 165, 280, 30);
        form.add(priceField);

        addLabel(form, "Status:", 10, 205);
        statusBox = new JComboBox<>(new String[]{"Available", "Booked", "Maintenance"});
        statusBox.setBounds(10, 225, 280, 30);
        form.add(statusBox);

        addBtn = new JButton("ADD");
        addBtn.setBounds(10, 275, 85, 32);
        addBtn.setBackground(new Color(39, 174, 96));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false); addBtn.setBorderPainted(false);
        form.add(addBtn);

        updateBtn = new JButton("UPDATE");
        updateBtn.setBounds(105, 275, 85, 32);
        updateBtn.setBackground(new Color(230, 126, 34));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false); updateBtn.setBorderPainted(false);
        form.add(updateBtn);

        deleteBtn = new JButton("DELETE");
        deleteBtn.setBounds(200, 275, 85, 32);
        deleteBtn.setBackground(new Color(192, 57, 43));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false); deleteBtn.setBorderPainted(false);
        form.add(deleteBtn);

        panel.add(form);

        // Search & Buttons
        searchField = new JTextField();
        searchField.setBounds(330, 62, 200, 30);
        panel.add(searchField);

        JButton searchBtn = createSmallBtn("Search", new Color(41, 128, 185));
        searchBtn.setBounds(540, 62, 80, 30);
        panel.add(searchBtn);

        clearBtn = createSmallBtn("Clear", new Color(127, 140, 141));
        clearBtn.setBounds(630, 62, 70, 30);
        panel.add(clearBtn);

        homeBtn = createSmallBtn("🏠 Home", new Color(44, 62, 80));
        homeBtn.setBounds(880, 62, 90, 30);
        panel.add(homeBtn);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Room No", "Type", "Price/Night", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(330, 100, 650, 450);
        panel.add(scroll);

        add(panel);
        loadData("");

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
                roomNoField.setText(model.getValueAt(row, 1).toString());
                typeBox.setSelectedItem(model.getValueAt(row, 2).toString());
                priceField.setText(model.getValueAt(row, 3).toString());
                statusBox.setSelectedItem(model.getValueAt(row, 4).toString());
            }
        });

        addBtn.addActionListener(e -> addRoom());
        updateBtn.addActionListener(e -> updateRoom());
        deleteBtn.addActionListener(e -> deleteRoom());
        clearBtn.addActionListener(e -> clearForm());
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));
        homeBtn.addActionListener(e -> { new HomePage(loggedInUser); dispose(); });

        setVisible(true);
    }

    void addLabel(JPanel p, String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setBounds(x, y, 200, 20);
        p.add(l);
    }

    JButton createSmallBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    void addRoom() {
        String roomNo = roomNoField.getText().trim();
        String type = typeBox.getSelectedItem().toString();
        String price = priceField.getText().trim();
        String status = statusBox.getSelectedItem().toString();

        if (roomNo.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!"); return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO rooms (room_no, type, price, status) VALUES (?,?,?,?)");
            ps.setString(1, roomNo); ps.setString(2, type);
            ps.setDouble(3, Double.parseDouble(price)); ps.setString(4, status);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room Added!");
            loadData(""); clearForm();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void updateRoom() {
        if (selectedId == 0) { JOptionPane.showMessageDialog(this, "Select a room first!"); return; }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE rooms SET room_no=?, type=?, price=?, status=? WHERE id=?");
            ps.setString(1, roomNoField.getText()); ps.setString(2, typeBox.getSelectedItem().toString());
            ps.setDouble(3, Double.parseDouble(priceField.getText())); ps.setString(4, statusBox.getSelectedItem().toString());
            ps.setInt(5, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Room Updated!");
            loadData(""); clearForm();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void deleteRoom() {
        if (selectedId == 0) { JOptionPane.showMessageDialog(this, "Select a room first!"); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this room?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == 0) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM rooms WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Room Deleted!");
                loadData(""); clearForm();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    void loadData(String search) {
        model.setRowCount(0);
        try {
            PreparedStatement ps;
            if (search.isEmpty()) {
                ps = DBConnection.getConnection().prepareStatement("SELECT * FROM rooms ORDER BY id DESC");
            } else {
                ps = DBConnection.getConnection().prepareStatement(
                    "SELECT * FROM rooms WHERE room_no LIKE ? OR type LIKE ? OR status LIKE ?");
                ps.setString(1, "%" + search + "%"); ps.setString(2, "%" + search + "%"); ps.setString(3, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("room_no"), rs.getString("type"),
                    "₹" + rs.getDouble("price"), rs.getString("status")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void clearForm() {
        roomNoField.setText(""); priceField.setText(""); selectedId = 0;
        typeBox.setSelectedIndex(0); statusBox.setSelectedIndex(0);
    }
}