package frames;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Base64;

public class RegisterFrame extends JFrame {
    private JTextField loginFieldR;
    private JTextField emailFieldR;
    private JTextField firstNameFieldR;
    private JTextField lastNameFieldR;
    private JPasswordField passwordFieldR;
    private JPasswordField repeatPasswordFieldR;
    private JButton registerButtonR;
    private JPanel registerPanel;

    public User user;

    public RegisterFrame() {
        setContentPane(registerPanel);
        setTitle("register");
        setSize(450, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        registerButtonR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String firstName = firstNameFieldR.getText();
        String lastName = lastNameFieldR.getText();
        String login = loginFieldR.getText();
        String email = emailFieldR.getText();
        String password = String.valueOf(passwordFieldR.getPassword());
        String confirmPassword = String.valueOf(repeatPasswordFieldR.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || login.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password is not the same", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (checkUser(login, email) == false) {
            JOptionPane.showMessageDialog(this, "Is user with this login or email", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        password = encodeBase64(password);

        user = addNewUser(login, email, firstName, lastName, password);
        if (user != null) {
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Impossibile to register", "Try again", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkUser(String login, String email) {
        boolean var = true;
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT ID_user FROM users WHERE email=? OR login=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, login);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                var = false;
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return var;
    }

    private String encodeBase64(String password) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedPassword = encoder.encodeToString(password.getBytes());
        return encodedPassword;
    }

    private User addNewUser(String login, String email, String firstName, String lastName, String password) {
        User user = null;

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "INSERT INTO users (login, email, first_name, last_name, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            preparedStatement.setString(5, password);

            int addedRow = preparedStatement.executeUpdate();
            if (addedRow > 0) {
                user = new User(firstName, lastName, login, password, email);
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return user;
    }

}