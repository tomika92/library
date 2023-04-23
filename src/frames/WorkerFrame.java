package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WorkerFrame extends JFrame {
    private JLabel nameSurnameFrame;
    private JButton logoutButton;
    private JTable taksTable;
    private JButton addNew;
    private JPanel workerPanel;

    public WorkerFrame(){
        setContentPane(workerPanel);
        setTitle("worker panel");
        setSize(600,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        nameSurnameFrame.setText(changeNameSurname(Singleton.getInstance().getValue()));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private String changeNameSurname(int userID) {
        StringBuilder user = new StringBuilder();
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library","root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM users WHERE ID_user=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                user.append(resultSet.getString("first_name"));
                user.append(" ");
                user.append(resultSet.getString("last_name"));
            }
            stmt.close();
            con.close();
        }catch (Exception e) {
            System.out.println(e);
        }
        return user.toString();
    }
}
