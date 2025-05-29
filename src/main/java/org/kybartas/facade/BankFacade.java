package org.kybartas.facade;

import org.kybartas.Account.Account;
import org.kybartas.Account.AccountService;
import org.kybartas.Statement.Statement;
import org.kybartas.Statement.StatementService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BankFacade {

    private final AccountService accountService;
    private final StatementService statementService;

    public BankFacade(AccountService accountService, StatementService statementService) {
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

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null) {
            return statementService.calculateBalance(accountNumber, from, to);
        }

        return accountService.getBalance(accountNumber);
    }
}
