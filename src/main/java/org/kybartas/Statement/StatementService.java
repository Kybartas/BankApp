package org.kybartas.statement;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatementService {

    private final StatementRepository statementRepository;
    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public List<Statement> importCSVStatement(MultipartFile file) throws Exception {

        Path tempFile = Files.createTempFile("upload", ".csv");
        file.transferTo(tempFile.toFile());

        List<String[]> rawCSVData = CSVStatementProcessor.readRawCSV(tempFile);
        Files.delete(tempFile);
        List<String[]> filteredData = CSVStatementProcessor.filterSwedBankFormat(rawCSVData);
        List<Statement> statements = CSVStatementProcessor.convertToStatements(filteredData);

        return statementRepository.saveAll(statements);
    }

    public byte[] exportCSVStatement(String accountNumber, LocalDate from, LocalDate to) throws Exception {

        List<Statement> statements = new ArrayList<>();

        if (from != null && to != null) {
            statements = statementRepository.findByAccountNumberAndDateRange(accountNumber, from, to);
        } else {
            statements = statementRepository.findByAccountNumber(accountNumber);
        }

        return CSVStatementProcessor.writeStatementsToByteArray(statements);
    }

    public BigDecimal calculateBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null) {
            return statementRepository.calculateBalanceByDates(accountNumber, from, to);
        }
        return statementRepository.calculateBalanceOverall(accountNumber);
    }

    public BigDecimal calculateBalanceByIds(List<Long> ids) {

        return statementRepository.calculateBalanceByIds(ids);
    }
}
