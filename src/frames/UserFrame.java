package frames;

import connection.DbConnectionProvider;
import models.*;
import repository.CollectionRepository;
import repository.UserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
    private JButton refreshButton;

    public static final String ORDER_BUTTON_LABEL = "Order";

    public UserFrame() {
        setContentPane(userPanel);
        setTitle("user panel");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        nameSurnameField.setText(UserRepository.getNameSurname(UserDataSingleton.getInstance().getValue()));
        BorrowTable.setModel(getBorrowTable());
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
                searchBar(searchField, SearchTable);
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BorrowTable.setModel(getBorrowTable());
                SearchTable.setModel(getSearchTable());
                SearchTable.getColumn("order").setCellRenderer(new ButtonRenderer());
                SearchTable.getColumn("order").setCellEditor(new ButtonEditor(new JCheckBox()));
            }
        });
    }

    private void searchBar(JTextField searchField, JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> tr = new TableRowSorter<DefaultTableModel>(model);
        table.setRowSorter(tr);
        tr.setRowFilter(RowFilter.regexFilter(searchField.getText().trim()));
    }

    private TableModel getBorrowTable() {
        Object[][] rows = null;
        try {
            rows = CollectionRepository.getUserBorrowTable();
        } catch (Exception e) {
            System.out.println(e);
        }
        String[] column = {"type", "title", "author", "status", "final status end date"};
        return new DefaultTableModel(rows, column);
    }

    private TableModel getSearchTable() {
        Object[][] rows = null;
        try {
            rows = CollectionRepository.getUserSearchTable();
        } catch (Exception e) {
            System.out.println(e);
        }
        String[] column = {"type", "title", "number", "author", "year", "publisher", "time", "genre", "quantity", "order"};
        return new DefaultTableModel(rows, column);
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

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
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
            try {
                if (checkIfOrderPossible()) {
                    CollectionRepository.insertOrder(id);
                    JOptionPane.showMessageDialog(button,  "Ordered");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(button, e.getMessage());
            }

        }
        isPushed = false;
        return label;
    }

    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    private boolean checkIfOrderPossible() {
        boolean orderPossible = false;
        try {
            Connection con = DbConnectionProvider.provideDbConnection();
            Statement stmt = con.createStatement();

            if (UserRepository.checkIfCannotOrderMoreItems(con)) {
                JOptionPane.showMessageDialog(button, "You can't take out anymore items");
            } else if (CollectionRepository.checkIfItemUnavailable(con, id)) {
                JOptionPane.showMessageDialog(button, "There is no item");
            } else {
                orderPossible = true;
            }

            con.close();
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(button, e.getMessage());
        }
        return orderPossible;
    }
}