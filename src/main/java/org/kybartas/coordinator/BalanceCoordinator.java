package org.kybartas.coordinator;

import org.kybartas.account.AccountService;
import org.kybartas.statement.StatementService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BalanceCoordinator {

    private final AccountService accountService;
    private final StatementService statementService;
    public BalanceCoordinator(AccountService accountService, StatementService statementService) {
        this.accountService = accountService;
        this.statementService = statementService;
    }

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null) {
            return statementService.calculateBalance(accountNumber, from, to);
        }
        return accountService.getBalance(accountNumber);
    }
}
