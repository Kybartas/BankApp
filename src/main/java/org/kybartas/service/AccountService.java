package org.kybartas.service;

import org.kybartas.entity.Statement;
import org.kybartas.repository.StatementRepository;
import org.kybartas.util.CSVUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private final StatementRepository statementRepository;
    public AccountService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public void importCSV (MultipartFile file) throws Exception {

        Path tempFile = Files.createTempFile("upload", ".csv");
        file.transferTo(tempFile.toFile());

        List<String[]> rawCSVData = CSVUtil.readRawCSV(tempFile);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);
        Files.delete(tempFile);

        statementRepository.saveAll(statements);
    }

    public byte[] exportCSV(String accountNumber, LocalDate from, LocalDate to) throws Exception {

        List<Statement> statements = new ArrayList<>();

        if (from != null && to != null) {
            statements = statementRepository.findByAccountNumberAndDateRange(accountNumber, from, to);
        } else {
            statements = statementRepository.findByAccountNumber(accountNumber);
        }

        return CSVUtil.writeStatements(statements);
    }

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null) {
            return statementRepository.getBalanceWithDates(accountNumber, from, to);
        }

        return statementRepository.getBalanceAll(accountNumber);
    }
}
