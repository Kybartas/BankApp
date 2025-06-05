package org.kybartas.statement;

import org.kybartas.facade.BankFacade;
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
    private final BankFacade facade;
    public StatementController(StatementService statementService, BankFacade facade) {
        this.statementService = statementService;
        this.facade = facade;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCSV(
            @RequestParam("file") MultipartFile file) {

        try {
            facade.importCSV(file);
            return ResponseEntity.ok("account import success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("account import error : " + e.getMessage());
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
                    .body("account export error : " + e.getMessage());
        }
    }

    @GetMapping("/generateCSV")
    public ResponseEntity<?> generateCSV(
            @RequestParam("numberOfAccounts") int numberOfAccounts,
            @RequestParam("transactionsPerAccount") int transactionsPerAccount) {

    CSVStatementGenerator.generate(numberOfAccounts, transactionsPerAccount, "samples");
    return ResponseEntity.ok("File(s) generated successfully");
    }
}