package org.kybartas.controller;

import org.kybartas.entity.Account;
import org.kybartas.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * REST controller for handling bank account and statement operations
 */
@RestController
@RequestMapping("/api/statements")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Imports bank statement from CSV file and stores as Account object
     * @param file location of CSV file
     * @return success or error message
     */
    @PostMapping("/import")
    public ResponseEntity<String> uploadCSV(
            @RequestParam("file") MultipartFile file) {

        try {
            accountService.importCSV(file);
            return ResponseEntity.ok("Account import success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account import error : " + e.getMessage());
        }
    }

    /**
     * Exports Account statements to CSV file, optional date range filtering.
     * @param from optional start date for filtering
     * @param to optional end date for filtering
     * @return HTTP response containing CSV data for all accounts or error message
     */
    @GetMapping("/export")
    public ResponseEntity<?> exportCSV(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        try {
            byte[] csvData = accountService.exportCSV(accountNumber, from, to);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statements.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Account export error : " + e.getMessage());
        }
    }

    /**
     * Calculates balance for given account, optional date range filtering.
     * @param accountNumber account number to calculate balance for
     * @param from optional start date for filtering
     * @param to optional end date for filtering
     * @return HTTP response containing account balance for given account number or error message.
     */
    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        BigDecimal balance = accountService.getBalance(accountNumber, from, to);
        return ResponseEntity.ok(balance);
    }
}