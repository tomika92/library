package frames;

import models.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Arrays;

public class UserFrame extends JFrame {
    private JLabel nameSurnameField;
    private JButton logoutButton;
    private JTable BorrowTable;
    private JTextField searchField;
    private JTable SearchTable;
    private JPanel userPanel;
    private JScrollPane SearchPane;
    private JScrollPane BorrowPane;

    public static final String ORDER_BUTTON_LABEL = "Order";

    public UserFrame() {
        setContentPane(userPanel);
        setTitle("user panel");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        nameSurnameField.setText(getNameSurname(Singleton.getInstance().getValue()));
        SearchTable.setModel(getSearchTable());
        SearchTable.getColumn("order").setCellRenderer(new ButtonRenderer());
        SearchTable.getColumn("order").setCellEditor(new ButtonEditor(new JCheckBox()));

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                DefaultTableModel model = (DefaultTableModel) SearchTable.getModel();
                TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(model);
                SearchTable.setRowSorter(tr);
                tr.setRowFilter(RowFilter.regexFilter(searchField.getText().trim()));
            }
        });
    }

    private TableModel getSearchTable() {
        String type;
        Object[][] rows = new Object[0][];
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM collection WHERE quantity > 0";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                type = resultSet.getString("type");
                LibraryCollection position = LibraryCollectionFactory.getType(LibraryCollectionFactory.CollectionType.valueOf(type));
                position.fillValues(resultSet);
                Object[] tab = new Object[0];
                if ("BOOK".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Book) position).getAuthor(), String.valueOf(position.getYear()),
                            position.getPublisher(), " ", position.getGenre(), String.valueOf(position.getQuantity()), position.getID()};
                } else if ("MAGAZINE".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), String.valueOf(((Magazine) position).getNumber()), " ",
                            String.valueOf(position.getYear()), position.getPublisher(), " ", position.getGenre(), String.valueOf(position.getQuantity()), position.getID()};
                } else if ("AUDIOBOOK".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Audiobook) position).getAuthor(), String.valueOf(position.getYear()),
                            position.getPublisher(), String.valueOf(((Audiobook) position).getTime()), position.getGenre(), String.valueOf(position.getQuantity()), position.getID()};
                } else if ("FILM".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", " ", String.valueOf(position.getYear()), position.getPublisher(),
                            String.valueOf(((Film) position).getTime()), position.getGenre(), String.valueOf(position.getQuantity()), position.getID()};
                }
                rows = Arrays.copyOf(rows, rows.length + 1);
                rows[rows.length - 1] = tab;
            }

            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        String column[] = {"type", "title", "number", "author", "year", "publisher", "time", "genre", "quantity", "order"};
        return new DefaultTableModel(rows, column);
    }

    private String getNameSurname(int userID) {
        StringBuilder user = new StringBuilder();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM users WHERE ID_user=?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user.append(resultSet.getString("first_name"));
                user.append(" ");
                user.append(resultSet.getString("last_name"));
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return user.toString();
    }

}
class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : UserFrame.ORDER_BUTTON_LABEL);
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;

    private String label;

    private boolean isPushed;

    private Integer id;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
            id = (Integer) value;
        }
        label = UserFrame.ORDER_BUTTON_LABEL;
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            boolean isPossibile = checkIsPossible();
            if(isPossibile == true){
                insertOrder();
            }
        }
        isPushed = false;
        return new String(label);
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    private boolean checkIsPossible(){
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT books_nr FROM users WHERE ID_user = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, Singleton.getInstance().getValue());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                if (resultSet.getInt("books_nr") == 0){
                    JOptionPane.showMessageDialog(button,  "You can't take out anymore items");
                    return false;
                }
            }
            sql = "SELECT quantity FROM collection WHERE ID_zbior = ?";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(resultSet.getInt("quantity") == 0){
                    JOptionPane.showMessageDialog(button,  "There is no item");
                    return false;
                }
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    private void insertOrder(){
        int cos = 13;
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "UPDATE users SET books_nr = books_nr-1 WHERE ID_user = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, Singleton.getInstance().getValue());
            preparedStatement.executeUpdate();

            sql = "UPDATE collection SET quantity = quantity - 1 WHERE ID_zbior = ?";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            String sqlInsert = "INSERT INTO rentals (ID_zbior, ID_user) VALUES (?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert);
            preparedStatementInsert.setInt(1, id);
            preparedStatementInsert.setInt(2, Singleton.getInstance().getValue());
            preparedStatementInsert.executeUpdate();

            JOptionPane.showMessageDialog(button,  "Ordered");
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}