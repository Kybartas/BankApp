package org.kybartas.service;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.kybartas.entity.Statement;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatementService {

    // Reads entire csv file
    private List<String[]> readRawCSV(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    // Remakes statement to show required information
    private List<String[]> filterSwedTable(List<String[]> table) {
        int[] indexes = {0, 2, 3, 4, 5, 6, 7};

        table.remove(0);
        table.remove(0);
        table.remove(table.size() -1);
        table.remove(table.size() -1);
        table.remove(table.size() -1);
        table.remove(table.size() -1);

        List<String[]> filteredTable = new ArrayList<>();

        for(String[] row : table) {
            String[] f = Arrays.stream(indexes).mapToObj(i -> row[i]).toArray(String[]::new);
            filteredTable.add(f);
        }

        return filteredTable;
    }

    private List<Statement> convertToStatements(List<String[]> filteredData) {
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

    public List<Statement> importCSV (Path filePath) throws Exception {
        List<String[]> rawCSVData = readRawCSV(filePath);
        List<String[]> filteredData = filterSwedTable(rawCSVData);
        return convertToStatements(filteredData);
    }

    public  List<Statement> filterByDateRange(List<Statement> statements, LocalDate from, LocalDate to) {
        return statements.stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public  BigDecimal calculateBalance(List<Statement> statements, String accountNumber) {

        BigDecimal balance = BigDecimal.ZERO;
        for (Statement statement : statements) {
            if (statement.getAccountNumber().equals(accountNumber)) {
                if ("K".equals(statement.getType())) {
                    balance = balance.add(statement.getAmount());
                } else if ("D".equals(statement.getType())) {
                    balance = balance.subtract(statement.getAmount());
                }
            }
        }
        return balance;
    }

    public void exportCSV(List<Statement> statements, Path filePath) throws Exception {
        List<String[]> rows = new ArrayList<>();

        rows.add(new String[] {"Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"});

        for (Statement statement : statements) {
            rows.add(new String[] {
                    statement.getAccountNumber(),
                    statement.getDate().toString(),
                    statement.getBeneficiary(),
                    statement.getDescription(),
                    statement.getAmount().toString(),
                    statement.getCurrency(),
                    statement.getType()
            });
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath.toString()))) {
            for (String[] row : rows) {
                writer.writeNext(row);
            }
        }
    }

    public void printStatements(List<Statement> statements) {
        for (Statement statement : statements) {
            statement.printStatement();
        }
    }

}
