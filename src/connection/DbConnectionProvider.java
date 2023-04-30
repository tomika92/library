package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionProvider {

    public static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/library";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "MyNewPass";

    public static Connection provideDbConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
