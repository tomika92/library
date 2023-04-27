package frames;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;


public class AddPositionFrame extends JFrame {
    private JTextField magNrField;
    private JTextField yearField;
    private JTextField genreField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField timeField;
    private JTextField quantityField;
    private JButton addButton;
    private JButton cancelButton;
    private JComboBox<String> typeBox;
    private JPanel addPositionPanel;

    public AddPositionFrame(){
        setContentPane(addPositionPanel);
        setTitle("add position panel");
        setSize(600, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        typeBox.addItem("BOOK");
        typeBox.addItem("AUDIOBOOK");
        typeBox.addItem("MAGAZINE");
        typeBox.addItem("FILM");
        setVisible(true);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewPosition();
                dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addNewPosition() {
        String type = (String) typeBox.getSelectedItem();
        String title = titleField.getText();
        String magNr = magNrField.getText();
        String author = authorField.getText();
        String year = yearField.getText();
        String publisher = publisherField.getText();
        String genre = genreField.getText();
        String time = timeField.getText();
        String quantity = quantityField.getText();

        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM collection";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                if("MAGAZINE".equals(resultSet.getString("type")) && type.equals(resultSet.getString("type"))){
                    ArrayList<String> magazineI = new ArrayList<>(Arrays.asList(title, magNr, year, publisher));
                    ArrayList<String> magazineR = new ArrayList<>(Arrays.asList(resultSet.getString("title"), resultSet.getString("mag_number"), resultSet.getString("year"), resultSet.getString("publisher")));
                    if(magazineI.equals(magazineR)){
                        updateDB(resultSet.getInt("ID_zbior"), quantity);
                    } else{
                        insertToDB(type, title, magNr, author, year, publisher, genre, time, quantity);
                    }
                    return;
                } else if ("FILM".equals(resultSet.getString("type")) && type.equals(resultSet.getString("type"))) {
                    ArrayList<String> filmI = new ArrayList<>(Arrays.asList(title, year, publisher));
                    ArrayList<String> filmR = new ArrayList<>(Arrays.asList(resultSet.getString("title"), resultSet.getString("year"), resultSet.getString("publisher")));
                    if(filmI.equals(filmR)){
                        updateDB(resultSet.getInt("ID_zbior"), quantity);
                    } else{
                        insertToDB(type, title, magNr, author, year, publisher, genre, time, quantity);
                    }
                    return;
                } else if(type.equals(resultSet.getString("type"))){
                    ArrayList<String> bookI = new ArrayList<>(Arrays.asList(type, title, author, year, publisher));
                    ArrayList<String> bookR = new ArrayList<>(Arrays.asList(resultSet.getString("type"), resultSet.getString("title"), resultSet.getString("author"), resultSet.getString("year"), resultSet.getString("publisher")));
                    if(bookI.equals(bookR)){
                        updateDB(resultSet.getInt("ID_zbior"), quantity);
                    } else{
                        insertToDB(type, title, magNr, author, year, publisher, genre, time, quantity);
                    }
                    return;
                }
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void updateDB(int id_zbior, String quantity) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            String sql = "UPDATE collection SET quantity = quantity + ? WHERE ID_zbior = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.valueOf(quantity));
            preparedStatement.setInt(2, id_zbior);
            preparedStatement.executeUpdate();
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void insertToDB(String type, String title, String magNr, String author, String year, String publisher, String genre, String time, String quantity) {
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/library", "root", "MyNewPass");
            Statement stmt = con.createStatement();
            if("BOOK".equals(type)){
                String sql = "INSERT INTO collection (type, title, author, `year`, publisher, genre, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, type);
                preparedStatement.setString(2, title);
                preparedStatement.setString(3, author);
                preparedStatement.setString(4, year);
                preparedStatement.setString(5, publisher);
                preparedStatement.setString(6, genre);
                preparedStatement.setString(7, quantity);
                preparedStatement.executeUpdate();
            } else if ("AUDIOBOOK".equals(type)) {
                String sqlA = "INSERT INTO collection (type, title, author, `year`, publisher, genre, quantity, `time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatementA = con.prepareStatement(sqlA);
                preparedStatementA.setString(1, type);
                preparedStatementA.setString(2, title);
                preparedStatementA.setString(3, author);
                preparedStatementA.setString(4, year);
                preparedStatementA.setString(5, publisher);
                preparedStatementA.setString(6, genre);
                preparedStatementA.setString(7, quantity);
                preparedStatementA.setString(8, time);
                preparedStatementA.executeUpdate();
            } else if ("MAGAZINE".equals(type)) {
                String sqlM = "INSERT INTO collection (type, title, mag_number, `year`, publisher, genre, quantity) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatementM = con.prepareStatement(sqlM);
                preparedStatementM.setString(1, type);
                preparedStatementM.setString(2, title);
                preparedStatementM.setString(3, magNr);
                preparedStatementM.setString(4, year);
                preparedStatementM.setString(5, publisher);
                preparedStatementM.setString(6, genre);
                preparedStatementM.setString(7, quantity);
                preparedStatementM.executeUpdate();
            } else {
                String sqlF = "INSERT INTO collection (type, title, `year`, publisher, genre, quantity, `time`) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatementF = con.prepareStatement(sqlF);
                preparedStatementF.setString(1, type);
                preparedStatementF.setString(2, title);
                preparedStatementF.setString(3, year);
                preparedStatementF.setString(4, publisher);
                preparedStatementF.setString(5, genre);
                preparedStatementF.setString(6, quantity);
                preparedStatementF.setString(7, time);
                preparedStatementF.executeUpdate();
            }
            stmt.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
