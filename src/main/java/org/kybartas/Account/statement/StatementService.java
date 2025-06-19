package org.kybartas.account.statement;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.kybartas.account.transaction.Transaction;
import org.kybartas.account.transaction.TransactionRepository;
import org.kybartas.account.AccountService;
import org.kybartas.exception.ExportException;
import org.kybartas.exception.ImportException;
import org.kybartas.exception.ReaderException;
import org.kybartas.exception.WriterException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
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

        try {
            Path tempFile = Files.createTempFile("upload", ".csv");
            file.transferTo(tempFile.toFile());
            List<String[]> rawCSVData = readCSVFile(tempFile);
            List<String[]> filteredData = filterSwedBankFormat(rawCSVData);
            List<Transaction> transactions = convertToTransactions(filteredData);
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
            return writeTransactionsToByteArray(statement);

        } catch (WriterException e) {
            throw new ExportException("Failed to export statements to file: " + e.getMessage(), e);
        }
    }

    private List<String[]> readCSVFile(Path filePath) throws ReaderException {

        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> data = csvReader.readAll();
            csvReader.close();
            return data;

        } catch (IOException e) {
            throw new ReaderException("Failed to read file: " + filePath + " ", e);
        } catch (CsvException e) {
            throw new ReaderException("Failed to parse file: " + filePath + " ", e);
        }
    }

    private List<String[]> filterSwedBankFormat(List<String[]> rows) {

        // Drop header
        rows.remove(0);

        return rows;
    }

    private List<Transaction> convertToTransactions(List<String[]> filteredData) {

        List<Transaction> transactions = new ArrayList<>();

        for (String[] row : filteredData) {

            Transaction t = new Transaction();
            t.setId(row[0]);
            t.setAccountNumber(row[1]);
            t.setDate(LocalDate.parse(row[2]));
            t.setBeneficiary(row[3]);
            t.setDescription(row[4]);
            t.setAmount(new BigDecimal(row[5]));
            t.setCurrency(row[6]);
            t.setType(row[7]);

            transactions.add(t);
        }

        return transactions;
    }

    private byte[] writeTransactionsToByteArray(List<Transaction> transactions) throws WriterException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

            writer.writeNext(new String[] {
                    "ID", "Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"
            });

            for (Transaction t : transactions) {
                writer.writeNext(new String[] {
                        t.getId(),
                        t.getAccountNumber(),
                        t.getDate().toString(),
                        t.getBeneficiary(),
                        t.getDescription(),
                        t.getAmount().toString(),
                        t.getCurrency(),
                        t.getType()
                });
            }

            writer.flush();

            return out.toByteArray();

        } catch (IOException e) {
            throw new WriterException("CSVWriter failed: " + e.getMessage(), e);
        }
    }
}
