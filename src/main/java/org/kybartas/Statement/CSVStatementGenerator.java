package org.kybartas.statement;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Random;

public class CSVStatementGenerator {

    private static final Random random = new Random();

    public static void generate(int numberOfAccounts, int transactionsPerAccount, String directory) {

        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (int accIndex = 0; accIndex < numberOfAccounts; accIndex++) {
            String accountNumber = String.format("%03d", accIndex);
            String filePath = directory + "/" + accountNumber + ".csv";

            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});
                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});

                for(int i = 0; i < transactionsPerAccount; i++) {
                    writer.writeNext(new String[] {
                            accountNumber,
                            " ",
                            LocalDate.now().minusDays(random.nextInt(365)).toString(),
                            "Beneficiary",
                            "Description",
                            String.format("%.2f", random.nextDouble() * 100),
                            "EUR",
                            random.nextBoolean() ? "K" : "D"
                    });
                }

                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});
                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});
                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
