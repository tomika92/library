package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class workerFrame extends JFrame {
    private JLabel nameSurnameFrame;
    private JButton logoutButton;
    private JTable taksTable;
    private JButton addNew;
    private JPanel workerPanel;

    public workerFrame(){
        setContentPane(workerPanel);
        setTitle("worker panel");
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
