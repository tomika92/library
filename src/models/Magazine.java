package models;

public class Magazine implements LibraryCollection {
    private String title;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumber(int number) {
        this.number = number;
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

    public void setType(String type) {
        this.type = type;
    }
}
