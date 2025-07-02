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

    public String openAccount() {

        long accountCount = accountRepository.count();
        Account newAccount = new Account("LT" + accountCount);

        Account savedAccount = accountRepository.save(newAccount);

        return savedAccount.getId();
    }

    public void processPayment(String senderAccountNumber, String recipientAccountNumber, BigDecimal amount) {

        Account sender = accountRepository.findAccountByAccountNumber(senderAccountNumber).orElseThrow();
        Account recipient = accountRepository.findAccountByAccountNumber(recipientAccountNumber).orElseThrow();

        Transaction senderTransaction = new Transaction();
        senderTransaction.setAccountNumber(senderAccountNumber);
        senderTransaction.setDate(LocalDate.now());
        senderTransaction.setBeneficiary(recipientAccountNumber);
        senderTransaction.setDescription("Description");
        senderTransaction.setAmount(amount);
        senderTransaction.setCurrency("EUR");
        senderTransaction.setType("D");

        Transaction recipientTransaction = new Transaction();
        recipientTransaction.setAccountNumber(recipientAccountNumber);
        recipientTransaction.setDate(LocalDate.now());
        recipientTransaction.setBeneficiary(senderAccountNumber);
        recipientTransaction.setDescription("Description");
        recipientTransaction.setAmount(amount);
        recipientTransaction.setCurrency("EUR");
        recipientTransaction.setType("K");

        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));

        transactionRepository.save(senderTransaction);
        transactionRepository.save(recipientTransaction);
        accountRepository.save(sender);
        accountRepository.save(recipient);
    }
}