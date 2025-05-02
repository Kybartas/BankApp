package org.kybartas.controller;

import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final StatementService statementService;
    private final List<Account> accounts = new ArrayList<>();

    @Autowired
    public StatementController(StatementService statementService) {

        this.statementService = statementService;
    }

    @PostMapping("/import")
    public ResponseEntity<List<Statement>> uploadCSV(@RequestParam("file") MultipartFile file) {

        try {
            Path tempFile = Files.createTempFile("upload", ".csv");
            file.transferTo(tempFile.toFile());

            List<Statement> newStatements = statementService.importCSV(tempFile);
            Account newAccount = new Account(newStatements.get(0).getAccountNumber(), newStatements);
            accounts.add(newAccount);

            Files.delete(tempFile);

            return new ResponseEntity<>(newAccount.getStatements(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCSV() {

        byte[] csvData = statementService.exportCSV(getAllStatements());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statements.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Statement>> filterCSV(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Statement> filtered = statementService.filterByDateRange(getAllStatements(), from, to);
        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    @GetMapping("/getBalance")
    public ResponseEntity<BigDecimal> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        Account selectedAccount = findAccountByNumber(accountNumber);

        List<Statement> accountStatements = selectedAccount.getStatements();

        if(from == null && to == null) {
            BigDecimal balance = selectedAccount.getBalance();
            return new ResponseEntity<>(balance, HttpStatus.OK);
        }

        BigDecimal balance = selectedAccount.getBalance(from, to);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    private List<Statement> getAllStatements() {
        return accounts.stream()
                .flatMap(account -> account.getStatements().stream())
                .toList();
    }

    private Account findAccountByNumber(String accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

}