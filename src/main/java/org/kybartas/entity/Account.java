package org.kybartas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Account {

    private final String accountNumber;
    private final List<Statement> statements;

    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
        this.statements = new ArrayList<>();
    }

    public Account(String accountNumber, List<Statement> statements) {
        this.accountNumber = accountNumber;
        this.statements = statements;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void addStatements(List<Statement> statements) {
        this.statements.addAll(statements);
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    // Utility method for getBalance
    private List<Statement> filterByDateRange(LocalDate from, LocalDate to) {

        return statements.stream()
                .filter(s -> !s.getDate().isBefore(from) && !s.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public BigDecimal getBalance(LocalDate from, LocalDate to) {

        List<Statement> tempStatements = statements;

        if (from != null && to != null) {
            tempStatements = filterByDateRange(from, to);
        }

        BigDecimal balance = BigDecimal.ZERO;

        for (Statement statement : tempStatements) {

            if ("K".equals(statement.getType())) {
                balance = balance.add(statement.getAmount());
            } else if ("D".equals(statement.getType())) {
                balance = balance.subtract(statement.getAmount());
            }
        }

        return balance;
    }

}
