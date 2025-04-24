package org.kybartas;

import org.kybartas.entity.Statement;
import org.kybartas.service.StatementService;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Statement> statements = new ArrayList<>();
        StatementService statementService= new StatementService();
        Path filePath = Path.of("statement.csv");

        try {
            statements = statementService.importCSV(filePath);
            statements = statementService.filterByDateRange(statements, LocalDate.of(2024,1,9), LocalDate.of(2024,2,9));
            statementService.exportCSV(statements, Path.of("exported.csv"));
            System.out.println(statementService.calculateBalance(statements, "LT047300010153866525").toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
