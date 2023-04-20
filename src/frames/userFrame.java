package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class userFrame extends JFrame{
    private JLabel nameSurnameField;
    private JButton logoutButton;
    private JTable BorrowTable;
    private JTextField searchField;
    private JTable SearchTable;
    private JPanel userPanel;


    public userFrame(){
        setContentPane(userPanel);
        setTitle("user panel");
        setSize(600,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
