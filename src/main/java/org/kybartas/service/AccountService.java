package org.kybartas.service;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
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
public class AccountService {

    //  Utility function for import CSV. Reads entire csv file
    private List<String[]> readRawCSV(Path filePath) throws Exception {
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                return csvReader.readAll();
            }
        }
    }

    // Utility function for import CSV. Remakes statement to show required information
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

    // Utility function for import CSV. Stores processed data as Statement objects
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

    public Account importCSV (Path filePath) throws Exception {
        List<String[]> rawCSVData = readRawCSV(filePath);
        List<String[]> filteredData = filterSwedTable(rawCSVData);
        List<Statement> statements = convertToStatements(filteredData);
        return new Account(statements.get(0).getAccountNumber(), statements);
    }

    public byte[] exportCSV(Account account) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

            writer.writeNext(new String[] {"Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"});

            for (Statement s : account.getStatements()) {
                writer.writeNext(new String[] {
                        s.getAccountNumber(),
                        s.getDate().toString(),
                        s.getBeneficiary(),
                        s.getDescription(),
                        s.getAmount().toString(),
                        s.getCurrency(),
                        s.getType()
                });
            }

            writer.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }

    public  List<Statement> filterByDateRange(List<Statement> statements, LocalDate from, LocalDate to) {
        return statements.stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public BigDecimal getBalance(Account account) {

        List<Statement> tempStatements = account.getStatements();

        BigDecimal balance = BigDecimal.ZERO;

        for (Statement statement : tempStatements) {

            if ("K".equals(statement.getType())) {
                balance = balance.add(statement.getAmount());
            } else if ("D".equals(statement.getType())) {
                balance = balance.subtract(statement.getAmount());
            }
        }

        return balance;
    }

    public BigDecimal getBalance(Account account, LocalDate from, LocalDate to) {

        List<Statement> tempStatements = account.getStatements();

        if (from != null && to != null) {
            tempStatements = filterByDateRange(tempStatements, from, to);
        }

        BigDecimal balance = BigDecimal.ZERO;

        for (Statement statement : tempStatements) {

            if ("K".equals(statement.getType())) {
                balance = balance.add(statement.getAmount());
            } else if ("D".equals(statement.getType())) {
                balance = balance.subtract(statement.getAmount());
            }
        }

        return balance;
    }

}
