package BankApi.user;


import BankApi.account.Account;
import BankApi.account.AccountRepository;
import BankApi.account.AccountService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    public UserService(UserRepository userRepository, AccountService accountService, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts(String name) {

        User user = userRepository.findByUsername(name).orElseThrow();
        List<String> accountIds = user.getAccountIds();
        List<Account> userAccounts = new ArrayList<>();

        for(String id: accountIds) {
            userAccounts.add(accountRepository.getAccountById(id));
        }

        return userAccounts;
    }

    public void openAccount(String name) {

        User user = userRepository.findByUsername(name).orElseThrow();
        String newAccountId = accountService.openAccount();
        user.addAccount(newAccountId);

        userRepository.save(user);
    }

}
