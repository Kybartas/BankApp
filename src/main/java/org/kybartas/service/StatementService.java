package org.kybartas.service;

import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.repository.AccountRepository;
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
public class StatementService {

    private final StatementRepository statementRepository;
    private final AccountRepository accountRepository;
    public StatementService(StatementRepository statementRepository, AccountRepository accountRepository) {
        this.statementRepository = statementRepository;
        this.accountRepository = accountRepository;
    }

    public void importCSVStatement(MultipartFile file) throws Exception {

        // 1. Process file, save to statement db
        Path tempFile = Files.createTempFile("upload", ".csv");
        file.transferTo(tempFile.toFile());

        List<String[]> rawCSVData = CSVUtil.readRawCSV(tempFile);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);
        Files.delete(tempFile);

        List<Statement> savedStatements = statementRepository.saveAll(statements);

        // 2. Create or update account
        String importedAccountNumber = statements.get(0).getAccountNumber();
        Account account = accountRepository.findById(importedAccountNumber).orElse(null);

        if (account == null) {
            account = new Account(importedAccountNumber);
            account.setBalance(statementRepository.getBalanceAll(importedAccountNumber));
            accountRepository.save(account);
        } else {
            List<Long> statementIds = savedStatements.stream().map(Statement::getId).toList();
            BigDecimal balanceDelta = statementRepository.getBalanceByIds(statementIds);
            account.setBalance(account.getBalance().add(balanceDelta));
            accountRepository.save(account);
        }

    }

    public byte[] exportCSVStatement(String accountNumber, LocalDate from, LocalDate to) throws Exception {

        List<Statement> statements = new ArrayList<>();

        if (from != null && to != null) {
            statements = statementRepository.findByAccountNumberAndDateRange(accountNumber, from, to);
        } else {
            statements = statementRepository.findByAccountNumber(accountNumber);
        }

        return CSVUtil.writeStatements(statements);
    }
}
