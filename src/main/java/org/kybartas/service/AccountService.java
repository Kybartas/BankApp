package org.kybartas.service;

import org.kybartas.entity.Account;
import org.kybartas.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account " + accountNumber + " not found"));
        return account.getBalance();
    }

}
