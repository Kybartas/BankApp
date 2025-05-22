package org.kybartas.entity;

import java.util.List;

public class Account {

    private final String accountNumber;
    private final List<Statement> statements;

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
}
