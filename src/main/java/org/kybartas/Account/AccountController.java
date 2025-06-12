package org.kybartas.account;

import org.kybartas.coordinator.BalanceCoordinator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final BalanceCoordinator balanceCoordinator;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AccountController(BalanceCoordinator balanceCoordinator, AccountRepository accountRepository, AccountService accountService) {
        this.balanceCoordinator = balanceCoordinator;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @GetMapping("/getAccounts")
    public ResponseEntity<?> getAccounts() {
        try{
            List<Account> accounts = accountRepository.findAll();
            return ResponseEntity.ok(accounts);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        try{
            BigDecimal balance = balanceCoordinator.getBalance(accountNumber, from, to);
            return ResponseEntity.ok(balance);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteAccounts")
    public ResponseEntity<String> deleteStatements() {

        accountService.deleteAccounts();
        return ResponseEntity.ok("Accounts deleted successfully");
    }
}
