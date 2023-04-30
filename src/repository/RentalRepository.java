package repository;

import connection.DbConnectionProvider;

import java.sql.*;
import java.time.LocalDate;

public class RentalRepository {

    public static void cancelExpired() throws Exception {
        LocalDate now = LocalDate.now();
        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT * from rentals WHERE NOT (status = 'returned' OR status = 'canceled')";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            if (("ordered".equals(resultSet.getString("status"))
                    && resultSet.getDate("order_end").before(Date.valueOf(now)))
                    || ("to_pick_up".equals(resultSet.getString("status"))
                    && resultSet.getDate("to_pick_up_end").before(Date.valueOf(now)))) {
                String sqlU = "UPDATE rentals SET status = 'canceled' WHERE ID_rentals = ?";
                PreparedStatement preparedStatementU = con.prepareStatement(sqlU);
                preparedStatementU.setInt(1, resultSet.getInt("ID_rentals"));
                preparedStatementU.executeUpdate();

                UserRepository.incrementBooksNumber(resultSet.getInt("ID_user"));
                CollectionRepository.incrementQuantity(resultSet.getInt("ID_zbior"));
            }
        }
        stmt.close();
        con.close();
    }

    public static void changeStatus(int idRentals) throws Exception{
        LocalDate now = LocalDate.now();

        Connection con = DbConnectionProvider.provideDbConnection();
        Statement stmt = con.createStatement();
        String sql = "SELECT * FROM rentals WHERE ID_rentals = ?";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.setInt(1, idRentals);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            if ("ordered".equals(resultSet.getString("status"))) {
                LocalDate date = now.plusDays(5);
                String sqlO = "UPDATE rentals SET status = 'to_pick_up', order_end = ?, to_pick_up_end = ? WHERE ID_rentals = ?";
                PreparedStatement preparedStatementO = con.prepareStatement(sqlO);
                preparedStatementO.setDate(1, Date.valueOf(now));
                preparedStatementO.setDate(2, Date.valueOf(date));
                preparedStatementO.setInt(3, idRentals);
                preparedStatementO.executeUpdate();
            } else if ("to_pick_up".equals(resultSet.getString("status"))) {
                LocalDate date = now.plusDays(21);
                String sqlP = "UPDATE rentals SET status = 'rented', to_pick_up_end = ?, rented_end = ? WHERE ID_rentals = ?";
                PreparedStatement preparedStatementP = con.prepareStatement(sqlP);
                preparedStatementP.setDate(1, Date.valueOf(now));
                preparedStatementP.setDate(2, Date.valueOf(date));
                preparedStatementP.setInt(3, idRentals);
                preparedStatementP.executeUpdate();
            } else if ("rented".equals(resultSet.getString("status"))) {
                String sqlR = "UPDATE rentals SET status = 'returned', rented_end = ?, returned_date = ? WHERE ID_rentals = ?";
                PreparedStatement preparedStatementR = con.prepareStatement(sqlR);
                preparedStatementR.setDate(1, Date.valueOf(now));
                preparedStatementR.setDate(2, Date.valueOf(now));
                preparedStatementR.setInt(3, idRentals);
                preparedStatementR.executeUpdate();

                UserRepository.incrementBooksNumber(resultSet.getInt("ID_user"));
                CollectionRepository.incrementQuantity(resultSet.getInt("ID_zbior"));
            }
        }
        stmt.close();
        con.close();
    }

}
