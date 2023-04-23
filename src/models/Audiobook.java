package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Audiobook implements LibraryCollection {
    private String title;
    private int ID;
    private String author;
    private int year;
    private String publisher;
    private String genre;
    private int quantity;
    private int time;
    private String type;

    public Audiobook() {}

    public String getAuthor() {
        return author;
    }

    public int getTime() {
        return time;
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
        this.author = set.getString("author");
        this.year = set.getInt("year");
        this.publisher = set.getString("publisher");
        this.time = set.getInt("time");
        this.genre = set.getString("genre");
        this.quantity = set.getInt("quantity");
        this.ID = set.getInt("ID_zbior");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
