package repository;

import connection.DbConnectionProvider;
import frames.UserDataSingleton;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserRepository {

    public static boolean checkIfCannotOrderMoreItems(Connection con) throws Exception {
        String sql = "SELECT books_nr FROM users WHERE ID_user = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, UserDataSingleton.getInstance().getValue());
        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next() && resultSet.getInt("books_nr") == 0;
    }

    public static String getNameSurname(int userID) {
        StringBuilder user = new StringBuilder();
        try {
            Connection con = DbConnectionProvider.provideDbConnection();
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

    public static void incrementBooksNumber(int userId) throws Exception {
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sqlU = "UPDATE users SET books_nr = books_nr + 1 WHERE ID_user = ?";
        PreparedStatement preparedStatementU = con.prepareStatement(sqlU);
        preparedStatementU.setInt(1, userId);
        preparedStatementU.executeUpdate();
        stmt.close();
        con.close();
    }

    public static User checkUserForLogin(String login, String password) throws Exception {
        User user;

        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM users WHERE password=? AND login=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, password);
        preparedStatement.setString(2, login);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            user = new User();
            user.setUserID(resultSet.getInt("ID_user"));
            user.setRole(resultSet.getString("role"));
        } else {
            user = new User();
            user.setUserID(0);
        }
        stmt.close();
        con.close();
        return user;
    }

    public static boolean checkUserForRegister(String login, String email) throws Exception {
        boolean var = true;

        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT ID_user FROM users WHERE email=? OR login=?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, email);
        preparedStatement.setString(2, login);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            var = false;
        }
        stmt.close();
        con.close();

        return var;
    }

    public static User addNewUser(String login, String email, String firstName, String lastName, String password) throws Exception{
        User user = null;
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO users (login, email, first_name, last_name, password) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, firstName);
        preparedStatement.setString(4, lastName);
        preparedStatement.setString(5, password);

        int addedRow = preparedStatement.executeUpdate();
        if (addedRow > 0) {
            user = new User(firstName, lastName, login, password, email);
        }
        stmt.close();
        con.close();

        return user;
    }
}
