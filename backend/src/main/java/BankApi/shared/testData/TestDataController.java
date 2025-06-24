package BankApi.shared.testData;

import BankApi.account.Account;
import BankApi.account.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/bankApi/testData")
public class TestDataController {

    private final TestDataService testDataService;
    private final AccountRepository accountRepository;

    public TestDataController(TestDataService testDataService, AccountRepository accountRepository) {
        this.testDataService = testDataService;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/populateDB")
    public ResponseEntity<String> populateDB() {

        testDataService.populateDB(5, 100);
        return ResponseEntity.ok("Database populated successfully");
    }

    @GetMapping("/getAllAccounts")
    public ResponseEntity<?> getAccounts() {

        List<Account> accounts = accountRepository.getAllAccountsOrdered();
        return ResponseEntity.ok(accounts);
    }
}