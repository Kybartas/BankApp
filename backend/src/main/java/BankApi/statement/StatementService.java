package BankApi.statement;

import com.opencsv.CSVWriter;
import BankApi.transaction.Transaction;
import BankApi.transaction.TransactionRepository;
import BankApi.shared.exception.ExportException;
import BankApi.shared.exception.WriterException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatementService {

    private final TransactionRepository transactionRepository;
    public StatementService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
