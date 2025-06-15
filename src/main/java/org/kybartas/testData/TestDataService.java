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
            for (int j = 0; j < transactionsPerAccount; j++) {
                accountService.newTransaction(generateRandomTransaction());
            }
        }
    }

    private Transaction generateRandomTransaction() {

        Random random = new Random();
        return new Transaction(
                String.valueOf((random.nextInt(100))),
                LocalDate.now().minusDays(random.nextInt(365)),
                "Beneficiary",
                "Description",
                BigDecimal.valueOf(random.nextDouble() * 100),
                "EUR",
                random.nextBoolean() ? "K" : "D"
        );
    }
}
