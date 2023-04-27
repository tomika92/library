package models;

public class User {
    public int userID;
    public String firstName;
    public String lastName;
    public String login;
    public String password;
    public String email;
    public String role;

    public User(){
    }
    public User(String firstName, String lastName, String login, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public String getRole() {
        return role;
    }
}