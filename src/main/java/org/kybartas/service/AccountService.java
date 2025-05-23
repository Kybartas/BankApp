package org.kybartas.service;

import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.repository.AccountRepository;
import org.kybartas.util.CSVUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Creates and fills Account object from CSV file
     * @param file CSV file
     * @throws Exception in case CSVReader fails
     */
    public void importCSV (MultipartFile file) throws Exception {

        Path tempFile = Files.createTempFile("upload", ".csv");
        file.transferTo(tempFile.toFile());

        List<String[]> rawCSVData = CSVUtil.readRawCSV(tempFile);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);
        Files.delete(tempFile);

        accountRepository.CreateTablesIfMissing();
        Account newAccount = new Account(statements.get(0).getAccountNumber(), statements);
        accountRepository.ImportAccount(newAccount);
    }

    /**
     * Generates a CSV bank statement for Account. Optional date range
     * @param accountNumber account to get statements of
     * @param from start date or null
     * @param to end date or null
     * @return byte array containing CSV statement data
     */
    public byte[] exportCSV(String accountNumber, LocalDate from, LocalDate to) throws Exception {

        Account account = accountRepository.getAccount(accountNumber);
        List<Statement> tempStatements = account.getStatements();

        if (from != null && to != null) {
            tempStatements = filterStatementsByDateRange(tempStatements, from, to);
        }

        return CSVUtil.writeStatements(tempStatements);
    }

    /**
     * Calculates balance of account by checking if transactions were K (credit) or D (debit). Optional date range.
     * @param accountNumber account to calculate balance of
     * @param from start date or null
     * @param to end date or null
     * @return balance of account
     */
    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        Account account = accountRepository.getAccount(accountNumber);
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
}
