package org.kybartas.service;

import com.opencsv.CSVWriter;
import org.jdbi.v3.core.Jdbi;
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

import static org.kybartas.util.JDBIUtil.CreateTablesIfMissing;
import static org.kybartas.util.JDBIUtil.ImportAccount;

@Service
public class AccountService {

    /**
     * Fills Account object with information from a CSV statements file
     * @param filePath path of CSV file
     * @return new Account object filled with information form CSV file
     * @throws Exception in case CSVReader fails
     */
    public Account importCSV (Path filePath) throws Exception {
        List<String[]> rawCSVData = CSVUtil.readRawCSV(filePath);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);

        CreateTablesIfMissing();

        Account newAccount = new Account(statements.get(0).getAccountNumber(), statements);

        ImportAccount(newAccount);

        return newAccount;
    }

    /**
     * Generates a CSV bank statement for Account
     * @param account Account object for statement generation
     * @return byte array containing CSV statement data
     */
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

    /**
     * Generates a CSV bank statement for Account for given date range
     * @param account Account object for statement generation
     * @param from start date
     * @param to end date
     * @return byte array containing CSV statement data
     */
    public byte[] exportCSV(Account account, LocalDate from, LocalDate to) {

        List<Statement> tempStatements = account.getStatements();

        if (from != null && to != null) {
            tempStatements = filterStatementsByDateRange(tempStatements, from, to);
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVWriter writer = new CSVWriter(new OutputStreamWriter(out));

            writer.writeNext(new String[] {"Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"});

            for (Statement s : tempStatements) {
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

    /**
     * Modifies given statement List to be within bounds of given date range
     * @param statements statements to filter
     * @param from start date
     * @param to end date
     * @return modified list of statements
     */
    public  List<Statement> filterStatementsByDateRange(List<Statement> statements, LocalDate from, LocalDate to) {

        return statements.stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    /**
     * Calculates balance of account by checking if transactions were K (credit) or D (debit).
     * @param account account to calculate balance of
     * @return balance of account
     */
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

    /**
     * Calculates balance of account within date range by checking if transactions were K (credit) or D (debit).
     * @param account account to calculate balance of
     * @return balance of account for date range
     */
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

    /**
     * Finds Account object in an Account list by account number.
     * @param accountNumber number of account to find
     * @return Account object with matching account number or null if not found.
     */
    public Account findAccountByNumber(List<Account> accounts, String accountNumber) {

        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }
}
