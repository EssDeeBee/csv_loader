package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionService {

    private static Connection connection;

    public static Connection getConnection() {
        try {
            String dbUserName = PropertiesService.getDbUserName();
            String dbPassword = PropertiesService.getDbPassword();
            String dbUrl = PropertiesService.getDbUrl();
            String dbDataBase = PropertiesService.getDbDataBaseName();
            String dbConnectionSettings = PropertiesService.getConnectionSettings();
            String driverName = PropertiesService.getDriverName();

            String connectionString = dbUrl + dbDataBase + "?user=" + dbUserName + "&password=" + dbPassword + dbConnectionSettings;

            Class.forName(driverName);
            return connection = (connection == null || connection.isClosed())
                    ? DriverManager.getConnection(connectionString)
                    : connection;
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void closeConnection() {
        try {
            if (!connection.isClosed())
                connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


}
