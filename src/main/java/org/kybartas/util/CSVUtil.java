package org.kybartas.util;

import com.opencsv.CSVReader;
import org.kybartas.entity.Statement;

import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVUtil {

    /**
     * Method for reading an entire CSV file via CSVReader.
     * @param filePath path of CSV file
     * @return all rows from CSV file as a List
     * @throws Exception in case of error while reading file
     */
    public static List<String[]> readRawCSV(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    /**
     * Method for reformatting CSV data into a workable format for the application.
     * Format is based on requirements of assignment.
     * Assumes a preset structure of statements exported from Swedbank, will fail otherwise.
     * @param rows list of rows from a basic reading of a CSV file
     * @return list of rows stripped of unneeded information
     */
    public static List<String[]> filterSwedTable(List<String[]> rows) {

        // 0 = account number, 2 = date, 3 = beneficiary, 4 = description, 5 = amount, 6 = currency, 7 = type
        int[] indexes = {0, 2, 3, 4, 5, 6, 7};

        // Drop headers and footers
        rows.remove(0);
        rows.remove(0);
        rows.remove(rows.size() -1);
        rows.remove(rows.size() -1);
        rows.remove(rows.size() -1);

        List<String[]> filteredRows = new ArrayList<>();

        for(String[] row : rows) {
            String[] f = Arrays.stream(indexes).mapToObj(i -> row[i]).toArray(String[]::new);
            filteredRows.add(f);
        }

        return filteredRows;
    }

    /**
     * Converts given data to statements
     * Expects data to be in a workable filtered format, will fail otherwise.
     * @param filteredData list of rows in an acceptable format currently provided by filterSwedTable method
     * @return list of statements filled from given data
     */
    public static List<Statement> convertToStatements(List<String[]> filteredData) {
        List<Statement> statements = new ArrayList<>();

        for (String[] row : filteredData) {
            Statement statement = new Statement();
            statement.setAccountNumber(row[0]);
            statement.setDate(LocalDate.parse(row[1]));
            statement.setBeneficiary(row[2]);
            statement.setDescription(row[3]);
            statement.setAmount(new BigDecimal(row[4]));
            statement.setCurrency(row[5]);
            statement.setType(row[6]);
            statements.add(statement);
        }

        return statements;
    }
}