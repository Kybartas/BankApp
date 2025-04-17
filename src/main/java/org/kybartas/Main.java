package org.kybartas;

import java.io.FileWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.time.StopWatch;

public class Main {

    // Completing basic functionality

    // Reads a CSV file and
    public static List<String[]> importCSV(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
               return csvReader.readAll();
            }
        }
    }

    // Exports table to CSV file
    public static void exportCSV(List<String[]> table, Path filePath) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toString()))) {
            for (String[] row : table) {
                writer.writeNext(row);
            }
        }
    }

    // Remakes given table to a format with only required information
    public static List<String[]> filterSwedTable(List<String[]> table) {
        int [] indexes = {0, 2, 3, 4, 5, 6, 7};

        List<String[]> tempTable = new ArrayList<>();

        for(String[] row : table){
            String[] f = Arrays.stream(indexes).mapToObj(i -> row[i]).toArray(String[]::new);
            tempTable.add(f);
        }

        tempTable.remove(1);
        tempTable.remove(tempTable.size()-1);
        tempTable.remove(tempTable.size()-1);
        tempTable.remove(tempTable.size()-1);


        return tempTable;
    }

    // Builds a new table for given date interval
    public static List<String[]> selectTableTimeFrame(List<String[]> table, LocalDate from, LocalDate to) {
        List<String[]> result = new ArrayList<>();

        for (String[] row : table) {
            try {
                LocalDate date = LocalDate.parse(row[1]);
                if ((date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to))) {
                    result.add(row);
                }
            } catch (Exception e) {
                result.add(row);
            }
        }

        return result;
    }

    // Calculates balance for given table and account number
    public static BigDecimal calculateBalance(List<String[]> table, String accountNumber) {

        BigDecimal balance = BigDecimal.ZERO;
        for (String[] row : table) {
            if (row[0].equals(accountNumber) && row[6].equals("K")) {
                balance = balance.add(new BigDecimal(row[4]));
            } else if (row[0].equals(accountNumber) && row[6].equals("D")) {
                balance = balance.subtract(new BigDecimal(row[4]));
            }
        }
        return balance;
    }

    // Displays table contents
    public static void printTable(List<String[]> table){
        for(String[] row : table){
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) {

        // Stopwatch to see impact on computing time while trying different approaches for insight (for fun mostly)
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        try {
            Path importPath = Path.of("statement.csv");
            Path exportPath = Path.of("exportedStatement.csv");

            List<String[]> table = importCSV(importPath);
            List<String[]> filteredTable = filterSwedTable(table);
            List<String[]> timedTable = selectTableTimeFrame(filteredTable,
                    LocalDate.of(2024,2,1), LocalDate.of(2024,2,11));

            printTable(timedTable);
            //exportCSV(filteredTable, exportPath);

            System.out.println("balance : " + calculateBalance(timedTable,"LT047300010153866525"));

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        stopwatch.stop();
        long timeTaken = stopwatch.getTime();
        System.out.println("Time taken: " + timeTaken + " ms");

    }
}