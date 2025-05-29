package org.kybartas.controller;

import org.kybartas.service.StatementService;
import org.kybartas.util.CSVGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final StatementService statementService;
    public StatementController(StatementService accountService) {
        this.statementService = accountService;
    }

    @PostMapping("/import")
    public ResponseEntity<String> uploadCSV(
            @RequestParam("file") MultipartFile file) {

        try {
            statementService.importCSVStatement(file);
            return ResponseEntity.ok("Account import success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Account import error : " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportCSV(
            @RequestParam("accountNumber") String accountNumber,
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to) {

        try {
            byte[] csvData = statementService.exportCSVStatement(accountNumber, from, to);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statements.csv");
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return ResponseEntity.ok().headers(headers).body(csvData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Account export error : " + e.getMessage());
        }
    }

    @GetMapping("/generateCSV")
    public ResponseEntity<?> generateCSV(
            @RequestParam("statementNum") int num) {

    CSVGenerator.generateCSV(num);
    return ResponseEntity.ok("File(s) generated successfully");
    }
}