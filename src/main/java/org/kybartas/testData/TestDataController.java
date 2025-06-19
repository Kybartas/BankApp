package org.kybartas.testData;

import org.kybartas.account.Account;
import org.kybartas.account.AccountRepository;
import org.kybartas.account.transaction.Transaction;
import org.kybartas.account.transaction.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/bankApi/testData")
public class TestDataController {

    private final TestDataService testDataService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TestDataController(TestDataService testDataService, AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {

        this.testDataService = testDataService;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/populateDB")
    public ResponseEntity<String> populateDB() {

        testDataService.populateDB(20, 40);
        return ResponseEntity.ok("Database populated successfully");
    }

    @GetMapping("/getAllTransactions")
    public ResponseEntity<?> getTransactions() {

        List<Transaction> transactions = transactionRepository.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/getAllAccounts")
    public ResponseEntity<?> getAccounts() {

        List<Account> accounts = accountRepository.findAll();
        return ResponseEntity.ok(accounts);
    }
}