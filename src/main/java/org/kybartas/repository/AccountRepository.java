package org.kybartas.repository;

import org.jdbi.v3.core.Jdbi;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;
import org.kybartas.repository.mapper.AccountMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class AccountRepository {

    private final Jdbi jdbi;
    public AccountRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void CreateTablesIfMissing () {

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

    public void ImportAccount(Account account) {

        List<Statement> statements = account.getStatements();

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
    }

    public Account getAccount(String accountNumber) {

        return jdbi.withHandle(handle -> {
            return handle.createQuery("SELECT * FROM accounts WHERE account_number = :accountNumber")
                    .bind("accountNumber", accountNumber)
                    .map(new AccountMapper(handle))
                    .findOne()
                    .orElse(null);
        });
    }

    public BigDecimal getBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null){
            return jdbi.withHandle(handle -> {
                return handle.createQuery("SELECT SUM(CASE" +
                                "WHEN type = 'K' THEN amount" +
                                "WHEN type = 'D' THEN -amount ELSE 0 END)" +
                                "FROM statements" +
                                "WHERE account_number = :accountNumber" +
                                "AND date >= :from AND date <= :to")
                        .bind("accountNumber", accountNumber)
                        .bind("from", from)
                        .bind("to", to)
                        .mapTo(BigDecimal.class)
                        .findOne()
                        .orElse(null);
            });
        }

        return jdbi.withHandle(handle -> {
            return handle.createQuery("SELECT SUM(CASE" +
                            "WHEN type = 'K' THEN amount" +
                            "WHEN type = 'D' THEN -amount ELSE 0 END)" +
                            "FROM statements" +
                            "WHERE account_number = :accountNumber ")
                    .bind("accountNumber", accountNumber)
                    .mapTo(BigDecimal.class)
                    .findOne()
                    .orElse(null);
        });
    }
}
