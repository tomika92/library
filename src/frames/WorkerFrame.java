package frames;

import repository.CollectionRepository;
import repository.RentalRepository;
import repository.UserRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

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
        nameSurnameFrame.setText(UserRepository.getNameSurname(UserDataSingleton.getInstance().getValue()));
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
        try {
            RentalRepository.cancelExpired();
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
        Object[][] rows = null;
        try {
            rows = CollectionRepository.getWorkerTaskTable();
        } catch (Exception e) {
            System.out.println(e);
        }
        String[] column = {"ID_user", "type", "title", "number", "author", "year", "publisher", "status", "final status end date", "action"};
        return new DefaultTableModel(rows, column);
    }

    private TableModel getSearchTable() {
        Object[][] rows = null;
        try {
            rows = CollectionRepository.getWorkerSearchTable();
        } catch (Exception e) {
            System.out.println(e);
        }
        String[] column = {"type", "title", "number", "author", "year", "publisher", "time", "genre", "quantity"};
        return new DefaultTableModel(rows, column);
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
        return label;
    }

    private void changeStatus() {
        LocalDate now = LocalDate.now();
        try {
            RentalRepository.changeStatus(id);
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