

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVComparator {


    public List<String> compareCSV(String filePath1, String filePath2) throws IOException {
        List<String> differences = new ArrayList<>();

        try (CSVParser parser1 = new CSVParser(new FileReader(filePath1), CSVFormat.DEFAULT.withFirstRecordAsHeader());
             CSVParser parser2 = new CSVParser(new FileReader(filePath2), CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            List<CSVRecord> records1 = parser1.getRecords();
            List<CSVRecord> records2 = parser2.getRecords();

            int maxRows = Math.max(records1.size(), records2.size());

            for (int i = 0; i < maxRows; i++) {
                CSVRecord record1 = i < records1.size() ? records1.get(i) : null;
                CSVRecord record2 = i < records2.size() ? records2.get(i) : null;

                if (record1 == null) {
                    differences.add("Row " + (i + 1) + " is missing in file1");
                } else if (record2 == null) {
                    differences.add("Row " + (i + 1) + " is missing in file2");
                } else if (!record1.toMap().equals(record2.toMap())) {
                    differences.add("Difference found in row " + (i + 1) + ": " + record1.toMap() + " != " + record2.toMap());
                }
            }
        }

        return differences;
    }

    public static void main(String[] args) throws IOException {
        CSVComparator comparator = new CSVComparator();
        List<String> differences = comparator.compareCSV("src/resources/csv/file1.csv", "src/resources/csv/file2.csv");
        differences.forEach(System.out::println);
    }
}

