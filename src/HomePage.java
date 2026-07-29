import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HomePage extends JFrame {

    String loggedInUser;

    public HomePage(String username) {
        this.loggedInUser = username;

        setTitle("Hotel Management System - Dashboard");
        setSize(700, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(236, 240, 241));

        // Header
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 700, 75);
        header.setBackground(new Color(44, 62, 80));

        JLabel title = new JLabel("🏨 Hotel Management System");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(241, 196, 15));
        title.setBounds(20, 15, 400, 35);
        header.add(title);

        JLabel welcome = new JLabel("Welcome, " + username + "!");
        welcome.setFont(new Font("Arial", Font.PLAIN, 13));
        welcome.setForeground(new Color(189, 195, 199));
        welcome.setBounds(510, 28, 180, 22);
        header.add(welcome);

        panel.add(header);

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        statsPanel.setBounds(20, 90, 650, 75);
        statsPanel.setBackground(new Color(236, 240, 241));
        statsPanel.add(createStat("🏠 Total Rooms", getCount("rooms"), new Color(41, 128, 185)));
        statsPanel.add(createStat("✅ Available", getAvailable(), new Color(39, 174, 96)));
        statsPanel.add(createStat("🔴 Booked", getBooked(), new Color(192, 57, 43)));
        statsPanel.add(createStat("👥 Guests", getCount("guests"), new Color(142, 68, 173)));
        panel.add(statsPanel);

        // Menu Buttons
        JButton roomBtn = createBtn("🏠 Rooms", new Color(41, 128, 185));
        roomBtn.setBounds(20, 195, 200, 65);
        panel.add(roomBtn);

        JButton guestBtn = createBtn("👥 Guests", new Color(39, 174, 96));
        guestBtn.setBounds(240, 195, 200, 65);
        panel.add(guestBtn);

        JButton bookingBtn = createBtn("📅 Bookings", new Color(142, 68, 173));
        bookingBtn.setBounds(460, 195, 200, 65);
        panel.add(bookingBtn);

        JButton billingBtn = createBtn("💰 Billing", new Color(230, 126, 34));
        billingBtn.setBounds(20, 280, 200, 65);
        panel.add(billingBtn);

        JButton checkoutBtn = createBtn("🚪 Check Out", new Color(192, 57, 43));
        checkoutBtn.setBounds(240, 280, 200, 65);
        panel.add(checkoutBtn);

        JButton logoutBtn = createBtn("🔒 Logout", new Color(44, 62, 80));
        logoutBtn.setBounds(460, 280, 200, 65);
        panel.add(logoutBtn);

        // Info Label
        JLabel info = new JLabel("Click on any option to manage hotel operations");
        info.setFont(new Font("Arial", Font.ITALIC, 13));
        info.setForeground(new Color(127, 140, 141));
        info.setBounds(170, 370, 400, 25);
        panel.add(info);

        add(panel);

        roomBtn.addActionListener(e -> {
            new RoomPage(loggedInUser);
                 dispose();
               });
        guestBtn.addActionListener(e -> {
    new GuestPage(loggedInUser); 
                dispose();
               });
        bookingBtn.addActionListener(e ->{
    new BookingPage(loggedInUser); 
                dispose(); 
              });
        billingBtn.addActionListener(e -> {
             new BillingPage(loggedInUser);
                 dispose(); 
              });
        checkoutBtn.addActionListener(e -> {
            new BookingPage(loggedInUser); 
               dispose();
               });
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (c == 0) { new LoginPage(); dispose(); }
        });

        setVisible(true);
    }

    JPanel createStat(String label, int count, Color color) {
        JPanel card = new JPanel(null);
        card.setBackground(color);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setBounds(8, 8, 150, 20);
        card.add(lbl);

        JLabel val = new JLabel(String.valueOf(count));
        val.setFont(new Font("Arial", Font.BOLD, 28));
        val.setForeground(Color.WHITE);
        val.setBounds(8, 28, 150, 38);
        card.add(val);

        return card;
    }

    int getCount(String table) {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM " + table);
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    int getAvailable() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM rooms WHERE status='Available'");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    int getBooked() {
        try {
            ResultSet rs = DBConnection.getConnection().createStatement()
                .executeQuery("SELECT COUNT(*) FROM rooms WHERE status='Booked'");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}