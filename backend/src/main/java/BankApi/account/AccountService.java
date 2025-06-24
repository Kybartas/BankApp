package BankApi.account;

import BankApi.transaction.Transaction;
import BankApi.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account getAccount(String accountNumber) throws AccountNotFoundException {

        return accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) throws AccountNotFoundException {

        if(from != null && to != null) {
            return transactionRepository.calculateAccountBalanceByDateRange(accountNumber, from, to);
        }
        return getAccount(accountNumber).getBalance();
    }

    public List<Transaction> getTransactions(String accountNumber, LocalDate from, LocalDate to) throws AccountNotFoundException {

        if (from != null && to != null) {
            return transactionRepository.getTransactionsByAccountNumberAndDateRange(accountNumber, from, to);
        }
        return transactionRepository.findTop20ByAccountNumberOrderByDateDesc(accountNumber);
    }

    public void newTransaction(Transaction transaction) {

        Account account;
        BigDecimal newBalance;

        try {
            account = getAccount(transaction.getAccountNumber());
        } catch (AccountNotFoundException e) {
            account = new Account(transaction.getAccountNumber());
        }

        if (transaction.getType().equals("K")) {
            newBalance = account.getBalance().add(transaction.getAmount());
        } else {
            newBalance = account.getBalance().subtract(transaction.getAmount()) ;
        }

        account.setBalance(newBalance);
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }
}