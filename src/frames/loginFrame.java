package frames;

import models.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Base64;

public class loginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JPanel loginPanel;

    public User user;

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
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerFrame register = new registerFrame();
                register.setVisible(true);
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

        password = encodeBase64(password);
        //int userID = checkUser(login, password);
        user = checkUser(login,password);

        if(user.getUserID() == 0){
            JOptionPane.showMessageDialog(this, "Login or password are incorrect", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Singleton singleton = Singleton.getInstance();
        singleton.setValue(user.getUserID());

        String roleD = "client";
        if(roleD.equals(user.getRole())){
            userFrame userF = new userFrame();
            userF.setVisible(true);
        }
        else{
            workerFrame workerF = new workerFrame();
            workerF.setVisible(true);
        }
        dispose();
    }

    private User checkUser(String login, String password) {
        //int userID = 0;
        User user = null;

        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library","root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM users WHERE password=? AND login=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                user = new User();
                user.userID = resultSet.getInt("ID_user");
                user.role = resultSet.getString("role");
            }
            stmt.close();
            con.close();
        }catch (Exception e) {
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
        loginFrame myLoginFrame = new loginFrame();
    }
}
