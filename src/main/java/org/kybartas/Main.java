package org.kybartas;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;
import org.apache.commons.lang3.time.StopWatch;

public class Main {

    // Reading a csv file and returning the data as a list of arrays of words (a table)
    public static List<String[]> readCSV(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
               return csvReader.readAll();
            }
        }
    }

    // Displaying needed information as per homework requirements
    // For now assuming different banks may have different formatting and not implementing safety
    public static void printSwedData(List<String[]> table){

        int[] indexes = {0, 2, 3, 4, 5, 6};

        for(String[] row : table){
            String[] f = Arrays.stream(indexes)
                    .mapToObj(i -> row[i])
                    .toArray(String[]::new);
            System.out.println(Arrays.toString(f));
        }
    }

    public static void main(String[] args) {

        // Stopwatch to see impact on computing time while trying different approaches for insight (for fun mostly)
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        try {
            Path filePath = Path.of("statement.csv");

            List<String[]> table = readCSV(filePath);

            printSwedData(table);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        stopwatch.stop();
        long timeTaken = stopwatch.getTime();
        System.out.println("Time taken: " + timeTaken + " ms");

    }
}