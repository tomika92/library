package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Magazine implements LibraryCollection {
    private String title;
    private int ID;
    private int number;
    private int year;
    private String publisher;
    private String genre;
    private int quantity;
    private String type;

    public Magazine() {}

    public int getNumber() {
        return number;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public String getPublisher() {
        return publisher;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void fillValues(ResultSet set) throws SQLException {
        this.type = set.getString("type");
        this.title = set.getString("title");
        this.number = set.getInt("mag_number");
        this.year = set.getInt("year");
        this.publisher = set.getString("publisher");
        this.genre = set.getString("genre");
        this.quantity = set.getInt("quantity");
        this.ID = set.getInt("ID_zbior");
    }

    public void setType(String type) {
        this.type = type;
    }
}