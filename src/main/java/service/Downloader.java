package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Downloader implements Runnable {

    private String link;
    private File file;

    private void downloadFileFromLink() {

        file.getParentFile().mkdirs();
        try {
            URLConnection urlConnection = new URL(link).openConnection();
            try (
                    FileOutputStream saveFilePathStream = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream()
            ) {

                System.out.println(DateService.getCurrentDate() + " Downloading file \"" + file.getName() + "\"");
                int line;
                byte[] buffer = new byte[65536];
                while ((line = inputStream.read(buffer)) != -1) {
                    saveFilePathStream.write(buffer, 0, line);
                }

                System.out.println(DateService.getCurrentDate() + " File \"" + file.getName() + "\" downloaded");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void run() {
        downloadFileFromLink();
    }

    public Downloader(String link, File file) {
        this.link = link;
        this.file = file;

    }
}
