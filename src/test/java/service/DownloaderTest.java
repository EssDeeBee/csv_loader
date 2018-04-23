package service;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DownloaderTest {


    @Test
    void test() throws Exception{
        File saveFilePath = new File(System.getProperty("user.home"), "temp/downloaded_feeds/" + "whatever" + ".csv");
        File f = new File("C:\\Users\\ser\\temp\\downloaded_feeds\\feed_10_15_dollars.csv");
        Path path = Paths.get(f.getAbsolutePath());
        System.out.println(Files.deleteIfExists(path));
        System.out.println(f.canWrite());
        System.out.println(saveFilePath.getAbsolutePath());
    }

    @Test
    void deleteFile(String s, String b, Integer c, Long g,String bb) {
        File f = new File("C:\\Users\\ser\\temp\\downloaded_feeds\\feed_10_15_dollars.csv");
        System.out.println(f.getAbsolutePath());
        if (!f.isDirectory()) {
            if (f.delete())
                System.out.printf("Done");
        } else {
            System.out.printf("Error :(");
        }
        ;
    }

}