package service;


import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesService {

    private static Properties properties = new Properties();
    private static String CONNECTION_PROP_PATH = "resources/db_connection.properties";
    private static String FEEDS_LIST_PROP = "resources/feeds_list.properties";


    public static Properties getConnectionProperties() {
        try (FileReader fileReader = new FileReader(CONNECTION_PROP_PATH)) {
            properties.load(fileReader);
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Properties getFeedListProperies() {
        try (FileReader fileReader = new FileReader(FEEDS_LIST_PROP)) {
            properties.load(fileReader);
            return properties;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getDbUserName() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_user_name");

    }

    public static String getDbPassword() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_password");
    }

    public static String getDbUrl() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_url");
    }

    public static String getDbDataBaseName() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_data_base_name");
    }

    public static String getConnectionSettings() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_connection_settings");
    }

    public static String getDriverName() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_driver_name");
    }

    public static String getSchemaName() {
        Properties connectionProperties = getConnectionProperties();
        return connectionProperties.getProperty("db_schema_name");
    }

}
