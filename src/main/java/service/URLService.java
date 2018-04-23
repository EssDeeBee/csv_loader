package service;

import java.io.*;
import java.util.*;

public class URLService {

    public static List<File> downloadAllFeeds() {

        Properties properties = PropertiesService.getFeedListProperies();

        Set<Object> propertiesKeys = properties.keySet();
        ArrayList<File> downloadedFilesPath = new ArrayList<>();
        List<Thread> threads = new ArrayList<Thread>();

        for (Object key : propertiesKeys) {

            String linkFromProperty = properties.getProperty(key.toString());
            File saveFilePath = new File(System.getProperty("user.home"), "temp/downloaded_feeds/" + key.toString() + ".csv");

            downloadedFilesPath.add(saveFilePath);
            Downloader downloader = new Downloader(linkFromProperty, saveFilePath);

            Thread downloadFileThread = new Thread(downloader);
            downloadFileThread.start();
            threads.add(downloadFileThread);

        }
        try {
            for (Thread thread : threads)
                thread.join();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return downloadedFilesPath;

    }

    public static void deleteTempFiles(List<File> tempFiles) {

 //       try {
        for (File tempFile : tempFiles) {

            if (!tempFile.isDirectory()) {
                if (tempFile.delete()) {
                    System.out.println(DateService.getCurrentDate() + " File " + tempFile + " successfully deleted");
                } else {
                    System.out.println(DateService.getCurrentDate() + " File " + tempFile + " cannot be deleted, try to delete it manually ");
                }
            }
        }
//        }catch (IOException ex){
//            ex.printStackTrace();
//        }
    }
}
