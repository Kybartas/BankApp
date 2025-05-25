package org.kybartas.service;

import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.repository.AccountRepository;
import org.kybartas.util.CSVUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public void importCSV (MultipartFile file) throws Exception {

        Path tempFile = Files.createTempFile("upload", ".csv");
        file.transferTo(tempFile.toFile());

        List<String[]> rawCSVData = CSVUtil.readRawCSV(tempFile);
        List<String[]> filteredData = CSVUtil.filterSwedTable(rawCSVData);
        List<Statement> statements = CSVUtil.convertToStatements(filteredData);
        Files.delete(tempFile);

        accountRepository.CreateTablesIfMissing();
        Account newAccount = new Account(statements.get(0).getAccountNumber(), statements);
        accountRepository.ImportAccount(newAccount);
    }

    public byte[] exportCSV(String accountNumber, LocalDate from, LocalDate to) throws Exception {

        Account account = accountRepository.getAccount(accountNumber);
        List<Statement> tempStatements = account.getStatements();

        if (from != null && to != null) {
            tempStatements = filterStatementsByDateRange(tempStatements, from, to);
        }

        return CSVUtil.writeStatements(tempStatements);
    }

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {
        return accountRepository.getBalance(accountNumber, from, to);
    }

    public  List<Statement> filterStatementsByDateRange(List<Statement> statements, LocalDate from, LocalDate to) {

        return statements.stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .collect(Collectors.toList());
    }
}
