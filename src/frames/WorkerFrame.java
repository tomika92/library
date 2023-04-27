package frames;

import models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;

public class WorkerFrame extends JFrame {
    private JLabel nameSurnameFrame;
    private JButton logoutButton;
    private JTable SearchTable;
    private JButton addNew;
    private JPanel workerPanel;
    private JScrollPane SearchPane;
    private JTextField searchField;
    private JTable TaskTable;
    private JScrollPane TeskPane;
    private JTextField searchTaskField;
    private JButton refreshButton;

    public static final String ACTION_BUTTON_LABEL = "Change status";

    public WorkerFrame() {
        setContentPane(workerPanel);
        setTitle("worker panel");
        setSize(1200, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        nameSurnameFrame.setText(getNameSurname(Singleton.getInstance().getValue()));
        cancelExpired();
        TaskTable.setModel(getTaskTable());
        TaskTable.getColumn("action").setCellRenderer(new ButtonRendererW());
        TaskTable.getColumn("action").setCellEditor(new ButtonEditorW(new JCheckBox()));
        SearchTable.setModel(getSearchTable());

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBar(searchField, SearchTable);
            }
        });
        searchTaskField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchBar(searchTaskField, TaskTable);
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TaskTable.setModel(getTaskTable());
                TaskTable.getColumn("action").setCellRenderer(new ButtonRendererW());
                TaskTable.getColumn("action").setCellEditor(new ButtonEditorW(new JCheckBox()));
                SearchTable.setModel(getSearchTable());
            }
        });
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddPositionFrame add = new AddPositionFrame();
                add.setVisible(true);
            }
        });
    }

    private void cancelExpired() {
        LocalDate now = LocalDate.now();
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * from rentals WHERE NOT (status = 'returned' OR status = 'canceled')";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                if(("ordered".equals(resultSet.getString("status")) && resultSet.getDate("order_end").before(Date.valueOf(now))) || ("to_pick_up".equals(resultSet.getString("status")) && resultSet.getDate("to_pick_up_end").before(Date.valueOf(now)))){
                    String sqlU = "UPDATE rentals SET status = 'canceled' WHERE ID_rentals = ?";
                    PreparedStatement preparedStatementU = con.prepareStatement(sqlU);
                    preparedStatementU.setInt(1, resultSet.getInt("ID_rentals"));
                    preparedStatementU.executeUpdate();
                    ButtonEditorW.changeQuantity(resultSet.getInt("ID_user"), resultSet.getInt("ID_zbior"));
                }
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void searchBar(JTextField searchField, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(tr);
        tr.setRowFilter(RowFilter.regexFilter(searchField.getText().trim()));
    }

    private TableModel getTaskTable() {
        Object[][] rows = new Object[0][];
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT rentals.ID_rentals, users.ID_user, collection.ID_zbior, collection.`type`, collection.title, collection.mag_number, collection.author, collection.`year`, collection.publisher, rentals.status, rentals.start_date, rentals.order_end, rentals.to_pick_up_end, rentals.rented_end, rentals.returned_date FROM ((rentals JOIN users ON users.ID_user = rentals.ID_user) JOIN collection ON rentals.ID_zbior = collection.ID_zbior) WHERE NOT (rentals.status = 'returned' OR rentals.status = 'canceled')";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Task position = new Task();
                position.fillValues(resultSet);
                Object[] tab = new Object[0];
                if(position.getToPickUpEndTime() == null){
                    tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                            position.getPublisher(), position.getStatus(), position.getOrderEndTime(), position.getRentalID()};
                } else if (position.getRentedEndTime() == null) {
                    tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                            position.getPublisher(), position.getStatus(), position.getToPickUpEndTime(), position.getRentalID()};
                } else if (position.getReturnedEndTime() == null) {
                    tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                            position.getPublisher(), position.getStatus(), position.getRentedEndTime(), position.getRentalID()};
                } else if (position.getReturnedEndTime() != null) {
                    tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                            position.getPublisher(), position.getStatus(), position.getReturnedEndTime(), position.getRentalID()};
                }
                rows = Arrays.copyOf(rows, rows.length + 1);
                rows[rows.length - 1] = tab;
            }
            stmt.close();
            con.close();
        }catch (Exception e) {
            System.out.println(e);
        }
        String column[] = {"ID_user", "type", "title", "number", "author", "year", "publisher",  "status", "final status end date", "action"};
        return new DefaultTableModel(rows, column);
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
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Book) position).getAuthor(), position.getYear(), position.getPublisher(), " ",
                            position.getGenre(), position.getQuantity()};
                } else if ("MAGAZINE".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), ((Magazine) position).getNumber(), " ", position.getYear(), position.getPublisher(), " ",
                            position.getGenre(), position.getQuantity()};
                } else if ("AUDIOBOOK".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", ((Audiobook) position).getAuthor(), position.getYear(), position.getPublisher(),
                            ((Audiobook) position).getTime(), position.getGenre(),position.getQuantity()};
                } else if ("FILM".equals(position.getType())) {
                    tab = new Object[]{position.getType(), position.getTitle(), " ", " ", position.getYear(), position.getPublisher(), ((Film) position).getTime(),
                            position.getGenre(), position.getQuantity()};
                }
                rows = Arrays.copyOf(rows, rows.length + 1);
                rows[rows.length - 1] = tab;
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        String column[] = {"type", "title", "number", "author", "year", "publisher", "time", "genre", "quantity"};
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

class ButtonRendererW extends JButton implements TableCellRenderer {

    public ButtonRendererW() {
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
        setText((value == null) ? "" : WorkerFrame.ACTION_BUTTON_LABEL);
        return this;
    }
}

class ButtonEditorW extends DefaultCellEditor {
    protected JButton button;

    private String label;

    private boolean isPushed;

    private Integer id;

    public ButtonEditorW(JCheckBox checkBox) {
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
        label = WorkerFrame.ACTION_BUTTON_LABEL;
        button.setText(label);
        isPushed = true;
        return button;
    }

    public Object getCellEditorValue() {
        if (isPushed) {
            changeStatus();
        }
        isPushed = false;
        return new String(label);
    }

    private void changeStatus() {
        LocalDate now = LocalDate.now();
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM rentals WHERE ID_rentals = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if("ordered".equals(resultSet.getString("status"))){
                    LocalDate date = now.plusDays(5);
                    String sqlO = "UPDATE rentals SET status = 'to_pick_up', order_end = ?, to_pick_up_end = ? WHERE ID_rentals = ?";
                    PreparedStatement preparedStatementO = con.prepareStatement(sqlO);
                    preparedStatementO.setDate(1, Date.valueOf(now));
                    preparedStatementO.setDate(2, Date.valueOf(date));
                    preparedStatementO.setInt(3, id);
                    preparedStatementO.executeUpdate();
                } else if ("to_pick_up".equals(resultSet.getString("status"))) {
                    LocalDate date = now.plusDays(21);
                    String sqlP = "UPDATE rentals SET status = 'rented', to_pick_up_end = ?, rented_end = ? WHERE ID_rentals = ?";
                    PreparedStatement preparedStatementP = con.prepareStatement(sqlP);
                    preparedStatementP.setDate(1, Date.valueOf(now));
                    preparedStatementP.setDate(2, Date.valueOf(date));
                    preparedStatementP.setInt(3, id);
                    preparedStatementP.executeUpdate();
                } else if ("rented".equals(resultSet.getString("status"))) {
                    String sqlR = "UPDATE rentals SET status = 'returned', rented_end = ?, returned_date = ? WHERE ID_rentals = ?";
                    PreparedStatement preparedStatementR = con.prepareStatement(sqlR);
                    preparedStatementR.setDate(1, Date.valueOf(now));
                    preparedStatementR.setDate(2, Date.valueOf(now));
                    preparedStatementR.setInt(3, id);
                    preparedStatementR.executeUpdate();
                    changeQuantity(resultSet.getInt("ID_user"), resultSet.getInt("ID_zbior"));
                }
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void changeQuantity(int user_ID, int collection_ID) {
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sqlU = "UPDATE users SET books_nr = books_nr + 1 WHERE ID_user = ?";
            PreparedStatement preparedStatementU = con.prepareStatement(sqlU);
            preparedStatementU.setInt(1, user_ID);
            preparedStatementU.executeUpdate();
            String sqlC = "UPDATE collection SET quantity = quantity + 1 WHERE ID_zbior = ?";
            PreparedStatement preparedStatementC = con.prepareStatement(sqlC);
            preparedStatementC.setInt(1,collection_ID);
            preparedStatementC.executeUpdate();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}