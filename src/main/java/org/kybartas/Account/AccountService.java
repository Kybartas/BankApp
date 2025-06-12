package org.kybartas.account;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public BigDecimal getBalance(String accountNumber) {
        return accountRepository.findById(accountNumber).get().getBalance();
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber).orElse(null);
    }

    public void updateOrCreateAccount(Account account) {
        accountRepository.save(account);
    }

    public void deleteAccounts() {
        accountRepository.deleteAll();
    }
}
