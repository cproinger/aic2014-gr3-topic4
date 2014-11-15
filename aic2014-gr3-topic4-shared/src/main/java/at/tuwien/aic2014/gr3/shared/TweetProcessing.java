package at.tuwien.aic2014.gr3.shared;

import java.io.IOException;
import java.sql.SQLException;

public interface TweetProcessing {
    void safeTweetIntoSQL(String rawJSON) throws IOException;
    void initializeSQLDatabase();
    void connectToSQL() throws Exception;
    void closeDownConnection() throws SQLException;
}
