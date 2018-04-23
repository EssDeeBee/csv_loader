package service;


import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseService {

    private static final char DELIMITER = ';';
    private static final String ENCODING = "UTF-8";

    private static CsvParser getCsvParser(FileInputStream fileInputStream) {

            CsvParserSettings csvParserSettings = new CsvParserSettings();
            csvParserSettings.getFormat().setDelimiter(DELIMITER);
            csvParserSettings.setHeaderExtractionEnabled(true);

            CsvParser csvParser = new CsvParser(csvParserSettings);
            csvParser.beginParsing(fileInputStream, ENCODING);

            return csvParser;

    }

    public static String getHeadersFromCsv(String csvPath) {
        try (FileInputStream fileInputStream = new FileInputStream(csvPath)) {

            CsvParser csvParser = getCsvParser(fileInputStream);

            String listOfHeaders = String.join(",", Arrays.asList(csvParser.getContext().headers()));

            csvParser.stopParsing();
            fileInputStream.close();
            return listOfHeaders;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static List<String> getRowsListFromCsv(String csvPath) {
        try (FileInputStream fileInputStream = new FileInputStream(csvPath)) {

            CsvParser csvParser = getCsvParser(fileInputStream);
            List<String> rowsList = new ArrayList<>();

            Record record;
            while ((record = csvParser.parseNextRecord()) != null) {
                rowsList.add(String.join(",", Arrays
                        .stream(record.getValues())
                        .map(e -> e == null ? e : e.replace("'", "''"))
                        .map(e -> "'" + e + "'")
                        .collect(Collectors.toList())));
            }
            csvParser.stopParsing();
            fileInputStream.close();
            return rowsList;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
