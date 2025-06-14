package org.kybartas.statement;

import org.kybartas.coordinator.ImportCoordinator;
import org.kybartas.exception.ExportException;
import org.kybartas.exception.ImportException;
import org.kybartas.statement.csv.CSVStatementGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/statements")
public class StatementController {

    private final StatementService statementService;
    private final ImportCoordinator importCoordinator;
    private final StatementRepository statementRepository;

    public StatementController(StatementService statementService, ImportCoordinator importCoordinator, StatementRepository statementRepository) {
        this.statementService = statementService;
        this.importCoordinator = importCoordinator;
        this.statementRepository = statementRepository;
    }

    @PostMapping("/import")
    public ResponseEntity<String> importCSV(
            @RequestParam("file") MultipartFile file) {

        try {
            importCoordinator.importCSV(file);
            return ResponseEntity.ok("Account import success");

        } catch (ImportException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to import CSV file : " + e.getMessage());
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

        } catch (ExportException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export data to CSV file : " + e.getMessage());
        }
    }

    @GetMapping("/getStatements")
    public ResponseEntity<?> getStatements() {

        List<Statement> statements = statementRepository.findAll();
        return ResponseEntity.ok(statements);
    }

    @GetMapping("/generateCSV")
    public ResponseEntity<?> generateCSV(
            @RequestParam("numberOfAccounts") int numberOfAccounts,
            @RequestParam("transactionsPerAccount") int transactionsPerAccount,
            @RequestParam(value = "directory", required = false, defaultValue = "samples") String directory) {

        CSVStatementGenerator.generate(numberOfAccounts, transactionsPerAccount, directory);
        return ResponseEntity.ok("File(s) generated successfully");
    }

    @GetMapping("/populateDB")
    public ResponseEntity<String> populateDB() {

        importCoordinator.populateDB(5, 5);
        return ResponseEntity.ok("Database populated successfully");
    }

    @DeleteMapping("/deleteStatements")
    public ResponseEntity<String> deleteStatements() {

        statementRepository.deleteAll();
        return ResponseEntity.ok("Statements deleted successfully");
    }
}