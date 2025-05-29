package org.kybartas.util;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Random;

public class CSVGenerator {

    private static final Random random = new Random();

    public static void generateCSV(int statementNum) {

        for (int accIndex = 0; accIndex < statementNum; accIndex++) {
            String accountNumber = String.format("%03d", accIndex);
            String filePath = "samples/" + accountNumber + ".csv";

            try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {

                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});
                writer.writeNext(new String[] {"Account Number", "Date"," ", "Beneficiary", "Description", "Amount", "Currency", "Type"});

                for(int i = 0; i < 10000; i++) {
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
