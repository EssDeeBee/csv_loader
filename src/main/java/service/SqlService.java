package service;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SqlService {

    private static final String COLUMN_TYPE = "text";
    private static final String DB_NAME = "postgres";
    private static final String SCHEMA_NAME = PropertiesService.getSchemaName();
    private static Connection connection = DBConnectionService.getConnection();

    public static void copyDataFromCsvFile(String tableName, String headers, FileReader fileReader) {

        try {

            String copyDataToDBQuery = "COPY " + SCHEMA_NAME + "." + tableName +
                    " (" + headers + ") FROM STDIN with ( FORMAT csv, delimiter E';', header  true )";
            String truncateTable = "truncate table " + SCHEMA_NAME+ "." + tableName;

            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            System.out.println(DateService.getCurrentDate() + " Executing " + truncateTable);
            statement.executeUpdate(truncateTable);
            System.out.println(DateService.getCurrentDate() + " Done");

            System.out.println(DateService.getCurrentDate() + " Executing " + copyDataToDBQuery);
            CopyManager copyManager = new CopyManager((BaseConnection) connection);
            copyManager.copyIn(copyDataToDBQuery, fileReader);
            System.out.println(DateService.getCurrentDate() + " Done");

            statement.close();
            connection.commit();
            System.out.println(DateService.getCurrentDate() + " Transaction committed");
        } catch (Exception ex) {
            try {
                System.out.println(DateService.getCurrentDate() + " Error! transaction rollbacked");
                connection.rollback();
                ex.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Boolean verifyTableAndColumns(String tableName, List<String> columns) {

        if (PropertiesService.getDriverName().toLowerCase().contains(DB_NAME.toLowerCase())) {
            if (isTableExist(tableName)) {
                List<String> columnsDistinct = getColumnsDistinction(tableName, columns);
                if (columnsDistinct.isEmpty()) {
                    return true;
                } else {
                    System.out.println(DateService.getCurrentDate()+" Distinct columns found. table = "+tableName+" columns = "+String.join(",",columnsDistinct) );
                    alterTable(tableName, columnsDistinct);
                    return true;
                }
            } else {
                createTable(tableName, columns);
                return true;
            }
        } else {
            System.out.println(DateService.getCurrentDate()+" Cannot create table for non postgresql database, manual creation required! ");
            return false;
        }
    }

    private static Boolean isTableExist(String tableName) {

        try (Statement statement = connection.createStatement()) {
            StringBuilder sqlCheckTable = new StringBuilder();
            sqlCheckTable.append("select exists (select * from information_schema.tables where table_name = '")
                    .append(tableName)
                    .append("'")
                    .append(" and table_schema ='")
                    .append(SCHEMA_NAME)
                    .append("');");

            ResultSet resultSet = statement.executeQuery(sqlCheckTable.toString());

            resultSet.next();
            return resultSet.getBoolean(1);

        } catch (SQLException ex) {
            System.out.println("Cannot check if the table exists. table = " + tableName + " schema = " + SCHEMA_NAME);
            ex.printStackTrace();
        }
        return false;
    }

    private static List<String> getColumnsDistinction(String tableName, List<String> columns) {

        try (Statement statement = connection.createStatement()) {

            StringBuilder sqlGetColumnsName = new StringBuilder();
            sqlGetColumnsName
                    .append("select column_name from information_schema.columns where table_name ='")
                    .append(tableName)
                    .append("' and table_schema = '")
                    .append(SCHEMA_NAME)
                    .append("' ;");

            ResultSet resultSet = statement.executeQuery(sqlGetColumnsName.toString());
            List<String> columnsFromDB = new ArrayList<>();
            while (resultSet.next()) {
                columnsFromDB.add(resultSet.getString("column_name"));
            }
            columns.removeAll(columnsFromDB);

            return columns;

        } catch (SQLException ex) {
            System.out.println(DateService.getCurrentDate() + " Cannot check if the table has the same columns. table = " + tableName + " schema = " + SCHEMA_NAME + " columns =" + String.join(", ", columns));
            ex.printStackTrace();
        }
        return null;
    }

    private static void createTable(String tableName, List<String> columns) {

        try (Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);
            StringBuilder sqlCreateTable = new StringBuilder();
            StringBuilder sqlDropIfExists = new StringBuilder();
            String columnsString = String.join(" " + COLUMN_TYPE + ",", columns) + " " + COLUMN_TYPE;

            sqlDropIfExists.append("drop table if exists ")
                    .append(tableName)
                    .append(" ;");

            System.out.println(DateService.getCurrentDate() + " Dropping table if exists using query: " + sqlDropIfExists);
            Integer dropResult = statement.executeUpdate(sqlDropIfExists.toString());
            System.out.println(DateService.getCurrentDate() + " " + dropResult);


            sqlCreateTable.append("create table ")
                    .append(SCHEMA_NAME)
                    .append(".")
                    .append(tableName)
                    .append(" (")
                    .append(columnsString)
                    .append(");");

            System.out.println(DateService.getCurrentDate() + " Creating table using query: " + sqlCreateTable);
            Integer createResult = statement.executeUpdate(sqlCreateTable.toString());
            System.out.println(DateService.getCurrentDate() + " " + createResult);

            connection.commit();

        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println(DateService.getCurrentDate() + " Cannot create table");
            ex.printStackTrace();
        }


    }

    private static void alterTable(String tableName, List<String> columns) {

        try (Statement statement = connection.createStatement()) {

            connection.setAutoCommit(false);
            List<String> addColumnsList = columns.stream().map(e -> "add column " + e +" text").collect(Collectors.toList());
            String addColumnString = String.join(",", addColumnsList);

            StringBuilder sqlAlterTable = new StringBuilder();
            sqlAlterTable.append("alter table ")
                    .append(SCHEMA_NAME)
                    .append(".")
                    .append(tableName)
                    .append(" ")
                    .append(addColumnString)
                    .append(" ;");

            System.out.println(DateService.getCurrentDate()+" Altering table using "+sqlAlterTable);
            Integer updateResult = statement.executeUpdate(sqlAlterTable.toString());
            System.out.println(DateService.getCurrentDate() + " " + updateResult);

        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        }
    }
}
