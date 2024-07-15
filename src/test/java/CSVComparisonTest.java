import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVComparisonTest {

    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonTest.class);

    @Test
    public void testCompareCSVFiles() {
        // Paths to CSV files
        String file1Path = "src/main/resources/csv1.csv";
        String file2Path = "src/main/resources/csv2.csv";

        // Compare CSV files
        List<String> differences = compareCSVFiles(file1Path, file2Path);

        // Log differences
        if (!differences.isEmpty()) {
            for (String difference : differences) {
                logger.debug(difference);
            }
        }

        // Assert no differences found
        Assert.assertTrue(differences.isEmpty(), "Differences found:\n" + String.join("\n", differences));
    }

    private List<String> compareCSVFiles(String file1Path, String file2Path) {
        List<String> differences = new ArrayList<>();

        try {
            // Read CSV files
            Reader reader1 = new FileReader(file1Path);
            Reader reader2 = new FileReader(file2Path);

            CSVParser parser1 = CSVFormat.DEFAULT.withHeader().parse(reader1);
            CSVParser parser2 = CSVFormat.DEFAULT.withHeader().parse(reader2);

            List<CSVRecord> records1 = parser1.getRecords();
            List<CSVRecord> records2 = parser2.getRecords();

            // Compare records
            for (int i = 0; i < Math.min(records1.size(), records2.size()); i++) {
                CSVRecord record1 = records1.get(i);
                CSVRecord record2 = records2.get(i);

                for (String header : parser1.getHeaderMap().keySet()) {
                    try {
                        String value1 = record1.isMapped(header) ? record1.get(header) : "";
                        String value2 = record2.isMapped(header) ? record2.get(header) : "";

                        if (!value1.equals(value2)) {
                            differences.add("Difference found at line " + (i + 1) + ", column '" + header + "': File1 -> " + value1 + ", File2 -> " + value2);
                        }
                    } catch (IllegalArgumentException e) {
                        logger.error("Error comparing CSV files at line " + (i + 1) + ", column '" + header + "': " + e.getMessage());
                    }
                }

                // Check for missing columns in File1
                for (String header : parser2.getHeaderMap().keySet()) {
                    if (!record1.isMapped(header)) {
                        differences.add("Missing column '" + header + "' in File1 at line " + (i + 1) + ".");
                    }
                }

                // Check for missing columns in File2
                for (String header : parser1.getHeaderMap().keySet()) {
                    if (!record2.isMapped(header)) {
                        differences.add("Missing column '" + header + "' in File2 at line " + (i + 1) + ".");
                    }
                }
            }

            // Check for extra rows
            if (records1.size() > records2.size()) {
                for (int i = records2.size(); i < records1.size(); i++) {
                    differences.add("Extra row found in File1 at line " + (i + 1) + ": File1 -> " + records1.get(i));
                }
            } else if (records2.size() > records1.size()) {
                for (int i = records1.size(); i < records2.size(); i++) {
                    differences.add("Extra row found in File2 at line " + (i + 1) + ": File2 -> " + records2.get(i));
                }
            }

            // Close resources
            reader1.close();
            reader2.close();
            parser1.close();
            parser2.close();

        } catch (Exception e) {
            logger.error("Error comparing CSV files", e);
        }

        return differences;
    }
}
