package BankApi.account;

import BankApi.transaction.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bankApi/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        try{
            BigDecimal balance = accountService.getBalance(accountNumber, from, to);
            return ResponseEntity.ok(balance);

        } catch(AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to find account: " + accountNumber + " " + e.getMessage());
        }
    }

    @GetMapping("/getTransactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        List<Transaction> transactions;
        try {
            transactions = accountService.getTransactions(accountNumber, from, to);
            return ResponseEntity.ok(transactions);

        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to find account: " + accountNumber + " " + e.getMessage());
        }
    }

    @GetMapping("/pay")
    public ResponseEntity<?> pay(
            @RequestParam("sender") String senderAccountNumber,
            @RequestParam("recipient") String recipientAccountNumber,
            @RequestParam("amount") BigDecimal amount) {

        try {
            accountService.processPayment(senderAccountNumber, recipientAccountNumber, amount);
            return ResponseEntity.ok("Payment successful!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}