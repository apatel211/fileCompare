
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class CSVComparisonTest {

    @Test
    public void testCsvComparison() throws IOException {
       CSVComparator comparator = new CSVComparator();
        List<String> differences = comparator.compareCSV("src/main/resources/csv1.csv", "src/main/resources/csv2.csv");

        // Print the differences for manual inspection
        System.out.println("Differences found:");
        differences.forEach(System.out::println);

        // Check if differences are found
        boolean areFilesIdentical = differences.isEmpty();

        // Provide a more detailed assertion message
        Assert.assertTrue(areFilesIdentical, "Files should be identical, but differences were found: " + differences);
    }
}
