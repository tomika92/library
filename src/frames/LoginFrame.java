package frames;

import models.User;
import repository.UserRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;
    public User user;

    public LoginFrame() {
        setContentPane(loginPanel);
        setTitle("login");
        setSize(450, 450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterFrame register = new RegisterFrame();
                register.setVisible(true);
            }
        });
    }

    private void loginUser() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());
        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter all field", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        password = encodeBase64(password);
        user = checkUser(login, password);
        if (user.getUserID() == 0) {
            JOptionPane.showMessageDialog(this, "Login or password are incorrect", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDataSingleton userDataSingleton = UserDataSingleton.getInstance();
        userDataSingleton.setValue(user.getUserID());
        String roleD = "client";
        if (roleD.equals(user.getRole())) {
            UserFrame userF = new UserFrame();
            userF.setVisible(true);
        } else {
            WorkerFrame workerF = new WorkerFrame();
            workerF.setVisible(true);
        }
        dispose();
    }

    private User checkUser(String login, String password) {
        User user = null;

        try {
            user = UserRepository.checkUserForLogin(login, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return user;
    }

    private String encodeBase64(String password) {
        Base64.Encoder encoder = Base64.getEncoder();
        String encodedPassword = encoder.encodeToString(password.getBytes());
        return encodedPassword;
    }

    public static void main(String[] args) {
        LoginFrame myLoginFrame = new LoginFrame();
    }
}