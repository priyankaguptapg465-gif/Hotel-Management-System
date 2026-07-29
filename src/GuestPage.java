import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GuestPage extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField nameField, phoneField, emailField, addressField, searchField;
    JButton addBtn, updateBtn, deleteBtn, clearBtn, homeBtn;
    int selectedId = 0;
    String loggedInUser;

    public GuestPage(String username) {
        this.loggedInUser = username;

        setTitle("Hotel - Guest Management");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 1000, 50);
        header.setBackground(new Color(39, 174, 96));
        JLabel title = new JLabel("👥 Guest Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        title.setBounds(15, 13, 280, 28);
        header.add(title);
        panel.add(header);

        // Form
        JPanel form = new JPanel(null);
        form.setBounds(10, 60, 310, 360);
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Guest Details"));

        addLabel(form, "Full Name:", 10, 25);
        nameField = new JTextField();
        nameField.setBounds(10, 45, 280, 30);
        form.add(nameField);

        addLabel(form, "Phone:", 10, 85);
        phoneField = new JTextField();
        phoneField.setBounds(10, 105, 280, 30);
        form.add(phoneField);

        addLabel(form, "Email:", 10, 145);
        emailField = new JTextField();
        emailField.setBounds(10, 165, 280, 30);
        form.add(emailField);

        addLabel(form, "Address:", 10, 205);
        addressField = new JTextField();
        addressField.setBounds(10, 225, 280, 30);
        form.add(addressField);

        addBtn = new JButton("ADD");
        addBtn.setBounds(10, 280, 85, 32);
        addBtn.setBackground(new Color(39, 174, 96));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false); addBtn.setBorderPainted(false);
        form.add(addBtn);

        updateBtn = new JButton("UPDATE");
        updateBtn.setBounds(105, 280, 85, 32);
        updateBtn.setBackground(new Color(230, 126, 34));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false); updateBtn.setBorderPainted(false);
        form.add(updateBtn);

        deleteBtn = new JButton("DELETE");
        deleteBtn.setBounds(200, 280, 85, 32);
        deleteBtn.setBackground(new Color(192, 57, 43));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false); deleteBtn.setBorderPainted(false);
        form.add(deleteBtn);

        panel.add(form);

        // Search
        searchField = new JTextField();
        searchField.setBounds(330, 62, 200, 30);
        panel.add(searchField);

        JButton searchBtn = createSmallBtn("Search", new Color(39, 174, 96));
        searchBtn.setBounds(540, 62, 80, 30);
        panel.add(searchBtn);

        clearBtn = createSmallBtn("Clear", new Color(127, 140, 141));
        clearBtn.setBounds(630, 62, 70, 30);
        panel.add(clearBtn);

        homeBtn = createSmallBtn("🏠 Home", new Color(44, 62, 80));
        homeBtn.setBounds(880, 62, 90, 30);
        panel.add(homeBtn);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "Email", "Address", "Joined"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(39, 174, 96));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(39, 174, 96));
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
                nameField.setText(model.getValueAt(row, 1).toString());
                phoneField.setText(model.getValueAt(row, 2).toString());
                emailField.setText(model.getValueAt(row, 3).toString());
                addressField.setText(model.getValueAt(row, 4).toString());
            }
        });

        addBtn.addActionListener(e -> addGuest());
        updateBtn.addActionListener(e -> updateGuest());
        deleteBtn.addActionListener(e -> deleteGuest());
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
        btn.setBackground(color); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    void addGuest() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields!"); return;
        }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "INSERT INTO guests (name, phone, email, address) VALUES (?,?,?,?)");
            ps.setString(1, name); ps.setString(2, phone);
            ps.setString(3, email); ps.setString(4, address);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest Added!");
            loadData(""); clearForm();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void updateGuest() {
        if (selectedId == 0) { JOptionPane.showMessageDialog(this, "Select a guest first!"); return; }
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                "UPDATE guests SET name=?, phone=?, email=?, address=? WHERE id=?");
            ps.setString(1, nameField.getText()); ps.setString(2, phoneField.getText());
            ps.setString(3, emailField.getText()); ps.setString(4, addressField.getText());
            ps.setInt(5, selectedId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Guest Updated!");
            loadData(""); clearForm();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    void deleteGuest() {
        if (selectedId == 0) { JOptionPane.showMessageDialog(this, "Select a guest first!"); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this guest?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == 0) {
            try {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement("DELETE FROM guests WHERE id=?");
                ps.setInt(1, selectedId); ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Guest Deleted!");
                loadData(""); clearForm();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    void loadData(String search) {
        model.setRowCount(0);
        try {
            PreparedStatement ps;
            if (search.isEmpty()) {
                ps = DBConnection.getConnection().prepareStatement("SELECT * FROM guests ORDER BY id DESC");
            } else {
                ps = DBConnection.getConnection().prepareStatement(
                    "SELECT * FROM guests WHERE name LIKE ? OR phone LIKE ? OR email LIKE ?");
                ps.setString(1, "%" + search + "%"); ps.setString(2, "%" + search + "%"); ps.setString(3, "%" + search + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("name"), rs.getString("phone"),
                    rs.getString("email"), rs.getString("address"), rs.getTimestamp("created_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    void clearForm() {
        nameField.setText(""); phoneField.setText("");
        emailField.setText(""); addressField.setText(""); selectedId = 0;
    }
}