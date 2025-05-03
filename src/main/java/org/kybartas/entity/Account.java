package org.kybartas.entity;

import java.util.ArrayList;
import java.util.List;

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
}
