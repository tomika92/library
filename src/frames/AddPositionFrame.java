package frames;

import connection.DbConnectionProvider;
import repository.CollectionRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import static repository.CollectionRepository.insertToDB;
import static repository.CollectionRepository.updateDB;


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
            Connection con = DbConnectionProvider.provideDbConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = CollectionRepository.getCollection(con);
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


}
