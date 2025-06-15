package org.kybartas.account.statement;

import org.kybartas.account.transaction.Transaction;
import org.kybartas.account.transaction.TransactionRepository;
import org.kybartas.account.AccountService;
import org.kybartas.exception.ExportException;
import org.kybartas.exception.ImportException;
import org.kybartas.exception.ReaderException;
import org.kybartas.exception.WriterException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatementService {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    public StatementService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public void importCSVStatement(MultipartFile file) throws ImportException {

        List<Transaction> importedStatement;

        try {
            Path tempFile = Files.createTempFile("upload", ".csv");
            file.transferTo(tempFile.toFile());
            List<String[]> rawCSVData = CSVStatementProcessor.readCSVFile(tempFile);
            List<String[]> filteredData = CSVStatementProcessor.filterSwedBankFormat(rawCSVData);
            List<Transaction> transactions = CSVStatementProcessor.convertToTransactions(filteredData);
            Files.delete(tempFile);

            for (Transaction transaction : transactions) {
                accountService.newTransaction(transaction);
            }

        } catch (IOException e) {
            throw new ImportException("Failed to process file: ", e);
        } catch (ReaderException e) {
            throw new ImportException("Failed to read or parse uploaded file: ", e);
        }
    }

    public byte[] exportCSVStatement(String accountNumber, LocalDate from, LocalDate to) throws ExportException{

        List<Transaction> statement;
        try {
            if (from != null && to != null) {
                statement = transactionRepository.getTransactionsByAccountNumberAndDateRange(accountNumber, from, to);
            } else {
                statement = transactionRepository.getTransactionsByAccountNumber(accountNumber);
            }
            return CSVStatementProcessor.writeTransactionsToByteArray(statement);

        } catch (WriterException e) {
            throw new ExportException("Failed to export statements to file: " + e.getMessage(), e);
        }
    }
}
