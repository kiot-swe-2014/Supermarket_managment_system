package supermarketmanagementsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public LoginForm() {
        setTitle("Login");
        setLayout(null);
        setSize(300, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Set background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // UI Components
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 20, 80, 25);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 165, 25);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 60, 165, 25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(passwordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(10, 100, 80, 25);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"admin", "seller"});
        roleComboBox.setBounds(100, 100, 165, 25);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        add(roleComboBox);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 140, 80, 25);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton);

        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateUser ();
            }
        });

        // Add padding to the components
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JButton) {
                ((JButton) component).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
        }

        setVisible(true);
    }

    private void authenticateUser () {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket_db", "root", ""); // Change to your DB credentials
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=? AND role=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (role.equals("admin")) {
                    new AdminDashboard();
                } else {
                    new SellerDashboard();
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login");
            }
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
