package org.kybartas.account;

import org.kybartas.workflow.BalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final BalanceService balanceService;
    private final AccountRepository accountRepository;

    public AccountController(BalanceService balanceCoordinator, AccountRepository accountRepository) {
        this.balanceService = balanceCoordinator;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/getAccounts")
    public ResponseEntity<?> getAccounts() {

        return ResponseEntity.ok(accountRepository.findAll());
    }

    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        try{
            BigDecimal balance = balanceService.getBalance(accountNumber, from, to);
            return ResponseEntity.ok(balance);

        } catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to find account: " + accountNumber + " " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAccounts")
    public ResponseEntity<String> deleteStatements() {

        accountRepository.deleteAll();
        return ResponseEntity.ok("Accounts deleted successfully");
    }
}