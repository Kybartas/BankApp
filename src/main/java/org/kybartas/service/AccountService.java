package org.kybartas.service;

import com.opencsv.CSVWriter;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.util.CSVUtil;
import org.kybartas.util.DBUtil;
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

    /**
     * Fills Account object with information from a CSV statements file
     * @param filePath path of CSV file
     * @throws Exception in case CSVReader fails
     */
    public void importCSV (Path filePath) throws Exception {
        List<String[]> rawCSVData = CSVUtil.readRawCSV(filePath);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);

        DBUtil.CreateTablesIfMissing();

        Account newAccount = new Account(statements.get(0).getAccountNumber(), statements);

        DBUtil.ImportAccount(newAccount);
    }

    /**
     * Generates a CSV bank statement for Account
     * @param accountNumber account to get statements of
     * @param from start date
     * @param to end date
     * @return byte array containing CSV statement data
     */
    public byte[] exportCSV(String accountNumber, LocalDate from, LocalDate to) {

        Account account = DBUtil.getAccount(accountNumber);
        List<Statement> tempStatements = account.getStatements();

        if (from != null && to != null) {
            tempStatements = filterStatementsByDateRange(tempStatements, from, to);
        }

        return CSVUtil.writeStatements(tempStatements);
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
    public BigDecimal getBalance(String accountNumber) {

        Account account = DBUtil.getAccount(accountNumber);
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
    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        Account account = DBUtil.getAccount(accountNumber);
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
