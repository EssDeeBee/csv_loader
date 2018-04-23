package main;

import service.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvDataLoader {

    public void getAndLoadDataFromCsv() {
        System.out.println(DateService.getCurrentDate() + "\n" + DateService.getCurrentDate() + " Csv data loader started");

        // Downloading all csv files from site. The list of files in feeds_list.properies
        System.out.println(DateService.getCurrentDate() + " Downloading files from the links");
        List<File> downloadedFilesPathList = URLService.downloadAllFeeds();
        System.out.println(DateService.getCurrentDate() + " Done");

        //Coping data from csv to DataBase
        for (File file : downloadedFilesPathList) {

            try(FileReader fileReader = new FileReader(file)) {
                String headers = ParseService.getHeadersFromCsv(file.getAbsolutePath());
                String tableName = file.getName().substring(0, file.getName().lastIndexOf('.'));

                List<String> headersList = new ArrayList<String>(Arrays.asList(headers.toLowerCase().split(",")));

                SqlService.verifyTableAndColumns(tableName, headersList);

                System.out.println(DateService.getCurrentDate() + " Coping data from " + file.getAbsolutePath());

                SqlService.copyDataFromCsvFile(tableName, headers, fileReader);
                System.out.println(DateService.getCurrentDate() + " Done");
            }catch (IOException ex){
                ex.printStackTrace();
            }

        }

        DBConnectionService.closeConnection();

        System.out.println(DateService.getCurrentDate() + " Deleting temp files... ");
        URLService.deleteTempFiles(downloadedFilesPathList);
        System.out.println(DateService.getCurrentDate() + " Done");

        System.out.println(DateService.getCurrentDate() + " Csv data loader finished \n"+DateService.getCurrentDate());
    }
}
