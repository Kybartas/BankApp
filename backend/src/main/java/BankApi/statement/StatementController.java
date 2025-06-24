package BankApi.statement;

import BankApi.shared.exception.ExportException;
import BankApi.shared.exception.ImportException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/bankApi/statement")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @PostMapping("/importCSV")
    public ResponseEntity<?> importCSV(
            @RequestParam("file") MultipartFile file) {

        try {
            statementService.importCSVStatement(file);
            return ResponseEntity.ok("Account import success");

        } catch (ImportException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to import CSV file : " + e.getMessage());
        }
    }

    @GetMapping("/exportCSV")
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to export CSV file : " + e.getMessage());
        }
    }
}