package org.kybartas.coordinator;

import org.kybartas.account.Account;
import org.kybartas.account.AccountService;
import org.kybartas.statement.Statement;
import org.kybartas.statement.StatementRepository;
import org.kybartas.statement.StatementService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class ImportCoordinator {

    private final AccountService accountService;
    private final StatementService statementService;
    private final StatementRepository statementRepository;
    public ImportCoordinator(AccountService accountService, StatementService statementService, StatementRepository statementRepository) {
        this.accountService = accountService;
        this.statementService = statementService;
        this.statementRepository = statementRepository;
    }

    public void importCSV(MultipartFile file) throws Exception {

        List<Statement> importedStatements = statementService.importCSVStatement(file);

        String importedAccountNumber = importedStatements.get(0).getAccountNumber();
        Account account = accountService.getAccount(importedAccountNumber);

        if (account == null) {
            account = new Account(importedAccountNumber);
            account.setBalance(statementService.calculateBalance(importedAccountNumber, null, null));
        } else {
            List<Long> statementIds = importedStatements.stream().map(Statement::getId).toList();
            BigDecimal balanceDelta = statementService.calculateBalanceByIds(statementIds);
            account.setBalance(account.getBalance().add(balanceDelta));
        }

        accountService.updateOrCreateAccount(account);
    }

    public void populateDB(int numberOfAccounts, int transactionsPerAccount) {

        List<Statement> statements = new ArrayList<>();
        Random random = new Random();

        for (int accIndex = 0; accIndex < numberOfAccounts; accIndex++) {
            String accountNumber = String.format("%03d", accIndex);

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
        }

        List<Statement> importedStatements = statementRepository.saveAll(statements);

        String importedAccountNumber = importedStatements.get(0).getAccountNumber();
        Account account = accountService.getAccount(importedAccountNumber);

        if (account == null) {
            account = new Account(importedAccountNumber);
            account.setBalance(statementService.calculateBalance(importedAccountNumber, null, null));
        } else {
            List<Long> statementIds = importedStatements.stream().map(Statement::getId).toList();
            BigDecimal balanceDelta = statementService.calculateBalanceByIds(statementIds);
            account.setBalance(account.getBalance().add(balanceDelta));
        }

        accountService.updateOrCreateAccount(account);

    }
}
