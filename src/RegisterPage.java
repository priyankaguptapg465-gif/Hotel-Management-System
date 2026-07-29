import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPage extends JFrame {

    JTextField usernameField, emailField;
    JPasswordField passwordField, confirmField;
    JButton registerBtn, backBtn;

    public RegisterPage() {
        setTitle("Hotel Management - Register");
        setSize(450, 430);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(44, 62, 80));

        JLabel title = new JLabel("🏨 Create Account");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(241, 196, 15));
        title.setBounds(120, 20, 250, 35);
        panel.add(title);

        addLabel(panel, "Username:", 70, 70);
        usernameField = createField();
        usernameField.setBounds(70, 93, 305, 33);
        panel.add(usernameField);

        addLabel(panel, "Email:", 70, 135);
        emailField = createField();
        emailField.setBounds(70, 158, 305, 33);
        panel.add(emailField);

        addLabel(panel, "Password:", 70, 200);
        passwordField = new JPasswordField();
        passwordField.setBounds(70, 223, 305, 33);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBackground(new Color(52, 73, 94));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        panel.add(passwordField);

        addLabel(panel, "Confirm Password:", 70, 265);
        confirmField = new JPasswordField();
        confirmField.setBounds(70, 288, 305, 33);
        confirmField.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmField.setBackground(new Color(52, 73, 94));
        confirmField.setForeground(Color.WHITE);
        confirmField.setCaretColor(Color.WHITE);
        panel.add(confirmField);

        registerBtn = new JButton("REGISTER");
        registerBtn.setBounds(70, 345, 140, 42);
        registerBtn.setBackground(new Color(241, 196, 15));
        registerBtn.setForeground(new Color(44, 62, 80));
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(registerBtn);

        backBtn = new JButton("BACK TO LOGIN");
        backBtn.setBounds(235, 345, 140, 42);
        backBtn.setBackground(new Color(52, 73, 94));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Arial", Font.BOLD, 13));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(backBtn);

        add(panel);

        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> { new 
            LoginPage(); 
            dispose(); });

        setVisible(true);
    }

    void addLabel(JPanel panel, String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBounds(x, y, 200, 22);
        panel.add(label);
    }

    JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(52, 73, 94));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    void doRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmField.getPassword()).trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!"); return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!"); return;
        }

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement check = con.prepareStatement("SELECT * FROM users WHERE username=?");
            check.setString(1, username);
            if (check.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Username already exists!"); return;
            }

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users (username, email, password) VALUES (?,?,?)");
            ps.setString(1, username); ps.setString(2, email); ps.setString(3, password);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
            new LoginPage(); 
            dispose();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}