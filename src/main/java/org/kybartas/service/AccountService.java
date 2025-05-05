package org.kybartas.service;

import com.opencsv.CSVWriter;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.util.CSVUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    public Account importCSV (Path filePath) throws Exception {
        List<String[]> rawCSVData = CSVUtil.readRawCSV(filePath);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);
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

    public byte[] exportCSV(Account account, LocalDate from, LocalDate to) {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

            writer.writeNext(new String[] {"Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"});

            List<Statement> filteredStatements = filterStatementsByDateRange(account.getStatements(), from, to);

            for (Statement s : filteredStatements) {
                writer.writeNext(new String[]{
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

    public  List<Statement> filterStatementsByDateRange(List<Statement> statements, LocalDate from, LocalDate to) {
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
            tempStatements = filterStatementsByDateRange(tempStatements, from, to);
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
