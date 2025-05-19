package org.kybartas.util;

import org.jdbi.v3.core.Jdbi;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;

import java.util.List;

public class JDBIUtil {

    public static void CreateTablesIfMissing() {

        Jdbi jdbi = Jdbi.create("jdbc:postgresql://db:5432/statement_db", "kristis", "kristis");

        jdbi.useTransaction(handle -> {

            handle.execute("""
                CREATE TABLE IF NOT EXISTS accounts(
                    account_number VARCHAR PRIMARY KEY
                )
            """);

            handle.execute("""
                CREATE TABLE IF NOT EXISTS statements(
                    account_number VARCHAR,
                    date DATE,
                    beneficiary VARCHAR,
                    description VARCHAR,
                    amount DOUBLE PRECISION,
                    currency VARCHAR,
                    type VARCHAR,
                    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
                )
            """);
        });
    }

    public static void ImportAccount(Account account) {

        List<Statement> statements = account.getStatements();

        Jdbi jdbi = Jdbi.create("jdbc:postgresql://db:5432/statement_db", "kristis", "kristis");

        try {

            jdbi.useTransaction(handle -> {

                handle.createUpdate("INSERT INTO accounts (account_number) VALUES (:accountNumber)")
                        .bind("accountNumber", account.getAccountNumber())
                        .execute();

                for (Statement statement : statements) {
                    handle.createUpdate("""
                    INSERT INTO statements (
                        account_number, date, beneficiary,
                        description, amount, currency, type
                    ) VALUES (
                        :accountNumber, :date, :beneficiary,
                        :description, :amount, :currency, :type
                    )
                """)
                            .bind("accountNumber", account.getAccountNumber())
                            .bind("date", statement.getDate())
                            .bind("beneficiary", statement.getBeneficiary())
                            .bind("description", statement.getDescription())
                            .bind("amount", statement.getAmount())
                            .bind("currency", statement.getCurrency())
                            .bind("type", statement.getType())
                            .execute();
                }
            });

        }catch (Exception e) {
            throw new RuntimeException("Account import to db failed", e);
        }
    }
}
