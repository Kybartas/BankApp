package org.kybartas.coordinator;

import org.kybartas.account.Account;
import org.kybartas.account.AccountService;
import org.kybartas.statement.Statement;
import org.kybartas.statement.StatementService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ImportCoordinator {

    private final AccountService accountService;
    private final StatementService statementService;
    public ImportCoordinator(AccountService accountService, StatementService statementService) {
        this.accountService = accountService;
        this.statementService = statementService;
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
}
