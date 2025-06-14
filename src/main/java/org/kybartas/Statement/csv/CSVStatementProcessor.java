package org.kybartas.statement.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.kybartas.exception.ReaderException;
import org.kybartas.exception.WriterException;
import org.kybartas.statement.Statement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVStatementProcessor {

    public static List<String[]> readRawCSV(Path filePath) throws ReaderException {

        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVReader csvReader = new CSVReader(reader);
            return csvReader.readAll();

        } catch (IOException e) {
            throw new ReaderException("Failed to read file: " + filePath + " ", e);
        } catch (CsvException e) {
            throw new ReaderException("Failed to parse file: " + filePath + " ", e);
        }
    }

    public static List<String[]> filterSwedBankFormat(List<String[]> rows) {

        // 0 = account number, 2 = date, 3 = beneficiary, 4 = description, 5 = amount, 6 = currency, 7 = type
        int[] indexes = {0, 2, 3, 4, 5, 6, 7};

        // Drop headers and footers
        rows.remove(0);
        rows.remove(0);
        rows.remove(rows.size() -1);
        rows.remove(rows.size() -1);
        rows.remove(rows.size() -1);

        List<String[]> filteredRows = new ArrayList<>();

        for(String[] row : rows) {
            String[] f = Arrays.stream(indexes).mapToObj(i -> row[i]).toArray(String[]::new);
            filteredRows.add(f);
        }

        return filteredRows;
    }

    public static List<Statement> convertToStatements(List<String[]> filteredData) {

        List<Statement> statements = new ArrayList<>();

        for (String[] row : filteredData) {
            Statement statement = new Statement();
            statement.setAccountNumber(row[0]);
            statement.setDate(LocalDate.parse(row[1]));
            statement.setBeneficiary(row[2]);
            statement.setDescription(row[3]);
            statement.setAmount(new BigDecimal(row[4]));
            statement.setCurrency(row[5]);
            statement.setType(row[6]);
            statements.add(statement);
        }

        return statements;
    }

    public static byte[] writeStatementsToByteArray(List<Statement> statements) throws WriterException{

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {

            //writer.writeNext(new String[] {"Account Number", "Date", "Beneficiary", "Description", "Amount", "Currency", "Type"});
            for (Statement s : statements) {
                writer.writeNext(new String[]{
                        s.getAccountNumber(),
                        s.getDate().toString(),
                        s.getBeneficiary(),
                        s.getDescription(),
                        s.getAmount().toString(),
                        s.getCurrency(),
                        s.getType()
                });
            }
            writer.close();
            return out.toByteArray();

        } catch (IOException e) {
            throw new WriterException("CSVWriter failed", e);
        }
    }
}