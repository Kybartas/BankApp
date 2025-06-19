package org.kybartas.testData;

import org.kybartas.account.AccountRepository;
import org.kybartas.account.AccountService;
import org.kybartas.account.transaction.Transaction;
import org.kybartas.account.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class TestDataService {

    private final AccountService accountService;
    public TestDataService(AccountService accountService, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountService = accountService;
    }

    public void populateDB(int numberOfAccounts, int transactionsPerAccount) {

        for (int i = 0; i < numberOfAccounts; i++) {

            LocalDate startDate = LocalDate.now().minusDays(transactionsPerAccount);

            for (int j = 0; j < transactionsPerAccount; j++) {

                LocalDate date = startDate.plusDays(j);
                accountService.newTransaction(generateRandomTransaction(i, date));
            }
        }
    }

    private Transaction generateRandomTransaction(int accountNum, LocalDate date) {

        Random random = new Random();
        Transaction t = new Transaction();
        t.setAccountNumber(String.format("%03d", accountNum));
        t.setDate(date);
        t.setBeneficiary("Beneficiary");
        t.setDescription("Description");
        t.setAmount(BigDecimal.valueOf(random.nextDouble() * 100));
        t.setCurrency("EUR");
        t.setType(random.nextBoolean() ? "K" : "D");

        return t;
    }
}
