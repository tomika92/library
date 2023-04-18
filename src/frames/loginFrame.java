package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class loginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;

    public loginFrame(){
        setContentPane(loginPanel);
        setTitle("login");
        setSize(450,450);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginUser();
                //TODO check that user exist and login action
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame register = new registerFrame();
                register.setVisible(true);
                setVisible(false);
            }
        });
    }

    private void loginUser() {
        String login = loginField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter all field", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public static void main(String[] args) {
        loginFrame myLoginFrame = new loginFrame();
    }
}
