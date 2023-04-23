package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface LibraryCollection {
    String getType();

    int getID();
    String getTitle();
    int getYear();
    String getPublisher();
    String getGenre();
    int getQuantity();
    void fillValues(ResultSet set) throws SQLException;
}
