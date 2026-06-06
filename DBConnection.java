package hrms;
import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    static {
        try {
            InputStream in = DBConnection.class
                .getResourceAsStream("config.properties");
            Properties props = new Properties();
            props.load(in);
            URL      = props.getProperty("db.url");
            USER     = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}