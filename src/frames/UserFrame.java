package frames;

//import models.Film;
import models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

class JTableButtonRenderer extends JButton implements TableCellRenderer {
    private TableCellRenderer defaultRenderer;
    public JTableButtonRenderer(TableCellRenderer renderer) {
        defaultRenderer = renderer;
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if(value instanceof Component)
            return (Component)value;
        return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

public class UserFrame extends JFrame {
    private JLabel nameSurnameField;
    private JButton logoutButton;
    private JTable BorrowTable;
    private JTextField searchField;
    private JTable SearchTable;
    private JPanel userPanel;

    public UserFrame() {
        setContentPane(userPanel);
        setTitle("user panel");
        TableCellRenderer tableRenderer;
        setSize(600, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        nameSurnameField.setText(getNameSurname(Singleton.getInstance().getValue()));
        SearchTable.setModel(getSearchTable());
        tableRenderer = SearchTable.getDefaultRenderer(JButton.class);
        SearchTable.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
        //SearchTable.getColumn("order").setCellRenderer(new JTableButtonRenderer(tableRenderer));

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private TableModel getSearchTable() {
        String type;
        Object[][] rows = new Object[0][];
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM collection";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                type = resultSet.getString("type");
                LibraryCollection position = LibraryCollectionFactory.getType(LibraryCollectionFactory.CollectionType.valueOf(type));
                position.fillValues(resultSet);
                Object[] tab = new Object[0];
                if ("BOOK".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Book) position).getAuthor(), String.valueOf(position.getYear()),
                            position.getPublisher(), " ", position.getGenre(), String.valueOf(position.getQuantity()), new JButton(String.valueOf(position.getID()))};
                } else if ("MAGAZINE".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), String.valueOf(((Magazine) position).getNumber()), " ",
                            String.valueOf(position.getYear()), position.getPublisher(), " ", position.getGenre(), String.valueOf(position.getQuantity()), new JButton(String.valueOf(position.getID()))};
                } else if ("AUDIOBOOK".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Audiobook) position).getAuthor(), String.valueOf(position.getYear()),
                            position.getPublisher(), String.valueOf(((Audiobook) position).getTime()), position.getGenre(), String.valueOf(position.getQuantity()), new JButton(String.valueOf(position.getID()))};
                } else if ("FILM".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", " ", String.valueOf(position.getYear()), position.getPublisher(),
                            String.valueOf(((Film) position).getTime()), position.getGenre(), String.valueOf(position.getQuantity()), new JButton(String.valueOf(position.getID()))};
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
