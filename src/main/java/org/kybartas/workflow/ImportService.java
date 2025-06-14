package org.kybartas.workflow;

import org.kybartas.account.Account;
import org.kybartas.account.AccountRepository;
import org.kybartas.account.AccountService;
import org.kybartas.exception.ImportException;
import org.kybartas.exception.ReaderException;
import org.kybartas.statement.Statement;
import org.kybartas.statement.StatementRepository;
import org.kybartas.statement.StatementService;
import org.kybartas.statement.csv.CSVStatementProcessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class ImportService {

    private final AccountService accountService;
    private final StatementService statementService;
    private final StatementRepository statementRepository;
    private final AccountRepository accountRepository;

    public ImportService(AccountService accountService, StatementService statementService, StatementRepository statementRepository, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.statementService = statementService;
        this.statementRepository = statementRepository;
        this.accountRepository = accountRepository;
    }

    public void importCSV(MultipartFile file) throws ImportException {

        List<Statement> importedStatements;

        try {
            Path tempFile = Files.createTempFile("upload", ".csv");
            file.transferTo(tempFile.toFile());
            List<String[]> rawCSVData = CSVStatementProcessor.readRawCSV(tempFile);
            List<String[]> filteredData = CSVStatementProcessor.filterSwedBankFormat(rawCSVData);
            List<Statement> statements = CSVStatementProcessor.convertToStatements(filteredData);
            Files.delete(tempFile);

            importedStatements = statementRepository.saveAll(statements);

        } catch (IOException e) {
            throw new ImportException("Failed to process file: ", e);
        } catch (ReaderException e) {
            throw new ImportException("Failed to read or parse uploaded file: ", e);
        }

        updateAccountFromStatements(importedStatements);
    }

    public void populateDB(int numberOfAccounts, int transactionsPerAccount) {

        for (int i = 0; i < numberOfAccounts; i++) {

            String accountNumber = String.format("%03d", accountRepository.count());
            List<Statement> statements = generateRandomStatementsForAccount(accountNumber, transactionsPerAccount);

            List<Statement> importedStatements = statementRepository.saveAll(statements);
            updateAccountFromStatements(importedStatements);
        }
    }
    
    private void updateAccountFromStatements(List<Statement> importedStatements) {

        String importedAccountNumber = importedStatements.get(0).getAccountNumber();
        Account account;

        try {
            account = accountService.getAccount(importedAccountNumber);

            List<Long> statementIds = importedStatements.stream().map(Statement::getId).toList();
            BigDecimal balanceDelta = statementService.calculateBalanceByIds(statementIds);
            account.setBalance(account.getBalance().add(balanceDelta));

        } catch (AccountNotFoundException e) {
            account = new Account(importedAccountNumber);
            account.setBalance(statementService.calculateBalance(importedAccountNumber, null, null));
        }

        accountRepository.save(account);
    }

    private List<Statement> generateRandomStatementsForAccount(String accountNumber, int transactionsPerAccount) {

        Random random = new Random();
        List<Statement> statements = new ArrayList<>();

        for (int i = 0; i < transactionsPerAccount; i++) {
            Statement statement = new Statement(
                    accountNumber,
                    LocalDate.now().minusDays(random.nextInt(365)),
                    "Beneficiary",
                    "Description",
                    BigDecimal.valueOf(random.nextDouble() * 100),
                    "EUR",
                    random.nextBoolean() ? "K" : "D"
            );
            statements.add(statement);
        }
        return statements;
    }
}