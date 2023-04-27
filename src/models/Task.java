package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Task {
    private int collectionID;
    private int rentalID;
    private int userID;
    private String title;
    private String type;
    private String author;
    private  String status;
    private  Date startTime;
    private Date orderEndTime;
    private Date toPickUpEndTime;
    private Date rentedEndTime;
    private Date returnedEndTime;
    private int magNr;
    private int year;
    private String publisher;

    public void fillValues(ResultSet set) throws SQLException {
        this.collectionID = set.getInt("ID_zbior");
        this.rentalID = set.getInt("ID_rentals");
        this.userID = set.getInt("ID_user");
        this.title = set.getString("title");
        this.type = set.getString("type");
        this.author = set.getString("author");
        this.status = set.getString("status");
        this.startTime = set.getDate("start_date");
        this.orderEndTime = set.getDate("order_end");
        this.toPickUpEndTime = set.getDate("to_pick_up_end");
        this.rentedEndTime = set.getDate("rented_end");
        this.returnedEndTime = set.getDate("returned_date");
        this.magNr = set.getInt("mag_number");
        this.year = set.getInt("year");
        this.publisher = set.getString("publisher");
    }

    public String getStatus() {
        return status;
    }

    public int getRentalID() {
        return rentalID;
    }

    public int getUserID() {
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public Date getOrderEndTime() {
        return orderEndTime;
    }

    public Date getToPickUpEndTime() {
        return toPickUpEndTime;
    }

    public Date getRentedEndTime() {
        return rentedEndTime;
    }

    public Date getReturnedEndTime() {
        return returnedEndTime;
    }

    public int getMagNr() {
        return magNr;
    }

    public int getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }
}