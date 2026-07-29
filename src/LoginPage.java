import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn, registerBtn;

    public LoginPage() {
        setTitle("Hotel Management System - Login");
        setSize(450, 360);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(44, 62, 80));

        // Title
        JLabel title = new JLabel("🏨 Hotel Management");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(241, 196, 15));
        title.setBounds(90, 25, 300, 35);
        panel.add(title);

        JLabel sub = new JLabel("Welcome! Please Login");
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(new Color(189, 195, 199));
        sub.setBounds(140, 60, 200, 20);
        panel.add(sub);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userLabel.setBounds(75, 100, 100, 25);
        panel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(75, 125, 295, 35);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBackground(new Color(52, 73, 94));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(241, 196, 15)));
        panel.add(usernameField);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        passLabel.setBounds(75, 170, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(75, 195, 295, 35);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(52, 73, 94));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(241, 196, 15)));
        panel.add(passwordField);

        // Buttons
        loginBtn = new JButton("LOGIN");
        loginBtn.setBounds(75, 255, 135, 42);
        loginBtn.setBackground(new Color(241, 196, 15));
        loginBtn.setForeground(new Color(44, 62, 80));
        loginBtn.setFont(new Font("Arial", Font.BOLD, 15));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(loginBtn);

        registerBtn = new JButton("REGISTER");
        registerBtn.setBounds(235, 255, 135, 42);
        registerBtn.setBackground(new Color(52, 73, 94));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 15));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(registerBtn);

        add(panel);

        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> {
            new RegisterPage();
            dispose();
        });

        setVisible(true);
    }

    void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                new HomePage(username);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}