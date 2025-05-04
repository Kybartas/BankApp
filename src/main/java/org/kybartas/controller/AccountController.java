package org.kybartas.controller;

import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class AccountController {

    private final AccountService accountService;
    private final List<Account> accounts = new ArrayList<>();

    @Autowired
    public AccountController(AccountService accountService) {

        this.accountService = accountService;
    }

    @PostMapping("/import")
    public ResponseEntity<Account> uploadCSV(@RequestParam("file") MultipartFile file) {

        try {
            Path tempFile = Files.createTempFile("upload", ".csv");
            file.transferTo(tempFile.toFile());

            Account newAccount = accountService.importCSV(tempFile);
            accounts.add(newAccount);

            Files.delete(tempFile);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCSV(
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        ByteArrayOutputStream accountsStreamCSV  = new ByteArrayOutputStream();

        try {
            for (Account account : accounts) {
                if(from != null && to != null) {
                    byte[] csvData = accountService.exportCSV(account, from, to);
                    accountsStreamCSV.write(csvData);
                } else if (from == null && to == null){
                    byte[] csvData = accountService.exportCSV(account);
                    accountsStreamCSV.write(csvData);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to write CSV data", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statements.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(accountsStreamCSV.toByteArray(), headers, HttpStatus.OK);
    }

    @GetMapping("/getBalance")
    public ResponseEntity<BigDecimal> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        Account selectedAccount = findAccountByNumber(accountNumber);

        if(from == null && to == null) {
            BigDecimal balance = accountService.getBalance(selectedAccount);
            return new ResponseEntity<>(balance, HttpStatus.OK);
        }

        BigDecimal balance = accountService.getBalance(selectedAccount, from, to);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    private Account findAccountByNumber(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

}