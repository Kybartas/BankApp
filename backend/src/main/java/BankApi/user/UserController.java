package BankApi.user;


import BankApi.account.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bankApi/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getAccounts")
    public ResponseEntity<?> getAccounts(
            @RequestParam("name") String name) {

        List<Account> accounts = userService.getAccounts(name);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/openAccount")
    public ResponseEntity<?> openAccount(
            @RequestParam("name") String name) {

        userService.openAccount(name);
        return ResponseEntity.ok("New account opened!");
    }
}