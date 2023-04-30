package repository;

import connection.DbConnectionProvider;
import frames.UserDataSingleton;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class CollectionRepository {

    public static boolean checkIfItemUnavailable(Connection con, int collectionId) throws Exception {
        String sql = "SELECT quantity FROM collection WHERE ID_zbior = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, collectionId);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getInt("quantity") == 0;
    }

    public static void insertOrder(int collectionId) throws Exception {
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "UPDATE users SET books_nr = books_nr-1 WHERE ID_user = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, UserDataSingleton.getInstance().getValue());
        preparedStatement.executeUpdate();

        sql = "UPDATE collection SET quantity = quantity - 1 WHERE ID_zbior = ?";
        preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, collectionId);
        preparedStatement.executeUpdate();

        String sqlInsert = "INSERT INTO rentals (ID_zbior, ID_user) VALUES (?, ?)";
        PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert);
        preparedStatementInsert.setInt(1, collectionId);
        preparedStatementInsert.setInt(2, UserDataSingleton.getInstance().getValue());
        preparedStatementInsert.executeUpdate();


        stmt.close();
        con.close();
    }

    public static Object[][] getUserSearchTable() throws Exception {
        Object[][] rows = new Object[0][];
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM collection WHERE quantity > 0";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String type = resultSet.getString("type");
            LibraryCollection position = LibraryCollectionFactory.getType(LibraryCollectionFactory.CollectionType.valueOf(type));
            position.fillValues(resultSet);
            Object[] tab = new Object[0];
            if ("BOOK".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", ((Book) position).getAuthor(), position.getYear(), position.getPublisher(), " ",
                        position.getGenre(), position.getQuantity(), position.getID()};
            } else if ("MAGAZINE".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), ((Magazine) position).getNumber(), " ", position.getYear(), position.getPublisher(), " ",
                        position.getGenre(), position.getQuantity(), position.getID()};
            } else if ("AUDIOBOOK".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", ((Audiobook) position).getAuthor(), position.getYear(), position.getPublisher(),
                        ((Audiobook) position).getTime(), position.getGenre(), position.getQuantity(), position.getID()};
            } else if ("FILM".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", " ", position.getYear(), position.getPublisher(), ((Film) position).getTime(),
                        position.getGenre(), position.getQuantity(), position.getID()};
            }
            rows = Arrays.copyOf(rows, rows.length + 1);
            rows[rows.length - 1] = tab;
        }
        stmt.close();
        con.close();
        return rows;
    }

    public static Object[][] getWorkerSearchTable() throws Exception {
        Object[][] rows = new Object[0][];
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM collection";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            String type = resultSet.getString("type");
            LibraryCollection position = LibraryCollectionFactory.getType(LibraryCollectionFactory.CollectionType.valueOf(type));
            position.fillValues(resultSet);
            Object[] tab = new Object[0];
            if ("BOOK".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", ((Book) position).getAuthor(), position.getYear(), position.getPublisher(), " ",
                        position.getGenre(), position.getQuantity()};
            } else if ("MAGAZINE".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), ((Magazine) position).getNumber(), " ", position.getYear(), position.getPublisher(), " ",
                        position.getGenre(), position.getQuantity()};
            } else if ("AUDIOBOOK".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", ((Audiobook) position).getAuthor(), position.getYear(), position.getPublisher(),
                        ((Audiobook) position).getTime(), position.getGenre(), position.getQuantity()};
            } else if ("FILM".equals(position.getType())) {
                tab = new Object[]{position.getType(), position.getTitle(), " ", " ", position.getYear(), position.getPublisher(), ((Film) position).getTime(),
                        position.getGenre(), position.getQuantity()};
            }
            rows = Arrays.copyOf(rows, rows.length + 1);
            rows[rows.length - 1] = tab;
        }
        stmt.close();
        con.close();
        return rows;
    }

    public static Object[][] getUserBorrowTable() throws Exception {
        Object[][] rows = new Object[0][];
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT rentals.ID_rentals, users.ID_user, collection.ID_zbior, collection.`type`, collection.title, collection.mag_number, collection.author, collection.`year`, collection.publisher, rentals.status, rentals.start_date, rentals.order_end, rentals.to_pick_up_end, rentals.rented_end, rentals.returned_date FROM ((rentals JOIN users ON users.ID_user = rentals.ID_user) JOIN collection ON rentals.ID_zbior = collection.ID_zbior) WHERE rentals.ID_user = ? AND NOT (rentals.status = 'returned' OR rentals.status = 'canceled')";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, UserDataSingleton.getInstance().getValue());
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Task position = new Task();
            position.fillValues(resultSet);
            Object[] tab = new Object[0];
            if (position.getToPickUpEndTime() == null) {
                tab = new Object[]{position.getType(), position.getTitle(), position.getAuthor(), position.getStatus(), position.getOrderEndTime()};
            } else if (position.getRentedEndTime() == null) {
                tab = new Object[]{position.getType(), position.getTitle(), position.getAuthor(), position.getStatus(), position.getToPickUpEndTime()};
            } else if (position.getReturnedEndTime() == null) {
                tab = new Object[]{position.getType(), position.getTitle(), position.getAuthor(), position.getStatus(), position.getRentedEndTime()};
            } else if (position.getReturnedEndTime() != null) {
                tab = new Object[]{position.getType(), position.getTitle(), position.getAuthor(), position.getStatus(), position.getReturnedEndTime()};
            }
            rows = Arrays.copyOf(rows, rows.length + 1);
            rows[rows.length - 1] = tab;
        }
        stmt.close();
        con.close();
        return rows;
    }

    public static Object[][] getWorkerTaskTable() throws Exception {
        Object[][] rows = new Object[0][];
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT rentals.ID_rentals, users.ID_user, collection.ID_zbior, collection.`type`, collection.title, collection.mag_number, collection.author, collection.`year`, collection.publisher, rentals.status, rentals.start_date, rentals.order_end, rentals.to_pick_up_end, rentals.rented_end, rentals.returned_date FROM ((rentals JOIN users ON users.ID_user = rentals.ID_user) JOIN collection ON rentals.ID_zbior = collection.ID_zbior) WHERE NOT (rentals.status = 'returned' OR rentals.status = 'canceled')";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Task position = new Task();
            position.fillValues(resultSet);
            Object[] tab = new Object[0];
            if (position.getToPickUpEndTime() == null) {
                tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                        position.getPublisher(), position.getStatus(), position.getOrderEndTime(), position.getRentalID()};
            } else if (position.getRentedEndTime() == null) {
                tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                        position.getPublisher(), position.getStatus(), position.getToPickUpEndTime(), position.getRentalID()};
            } else if (position.getReturnedEndTime() == null) {
                tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                        position.getPublisher(), position.getStatus(), position.getRentedEndTime(), position.getRentalID()};
            } else if (position.getReturnedEndTime() != null) {
                tab = new Object[]{position.getUserID(), position.getType(), position.getTitle(), position.getMagNr(), position.getAuthor(), position.getYear(),
                        position.getPublisher(), position.getStatus(), position.getReturnedEndTime(), position.getRentalID()};
            }
            rows = Arrays.copyOf(rows, rows.length + 1);
            rows[rows.length - 1] = tab;
        }
        stmt.close();
        con.close();
        return rows;
    }

    public static void incrementQuantity(int collectionId) throws Exception {
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sqlC = "UPDATE collection SET quantity = quantity + 1 WHERE ID_zbior = ?";
        PreparedStatement preparedStatementC = con.prepareStatement(sqlC);
        preparedStatementC.setInt(1, collectionId);
        preparedStatementC.executeUpdate();
        stmt.close();
        con.close();
    }

    public static ResultSet getCollection(Connection con) throws Exception {
        String sql = "SELECT * FROM collection";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public static void updateDB(int id_zbior, String quantity) throws Exception {
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "UPDATE collection SET quantity = quantity + ? WHERE ID_zbior = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.valueOf(quantity));
        preparedStatement.setInt(2, id_zbior);
        preparedStatement.executeUpdate();
        stmt.close();
        con.close();

    }

    public static void insertToDB(String type, String title, String magNr, String author, String year, String publisher, String genre, String time, String quantity) throws Exception {
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        if ("BOOK".equals(type)) {
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

    }
}
