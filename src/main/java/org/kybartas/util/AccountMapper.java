package org.kybartas.util;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AccountMapper implements RowMapper<Account> {

    private final Handle handle;

    public AccountMapper(Handle handle) {
        this.handle = handle;
    }

    @Override
    public Account map(ResultSet rs, StatementContext ctx) throws SQLException {

        String accountNumber = rs.getString("account_number");

        List<Statement> statements = handle.createQuery("""
            SELECT account_number, date, beneficiary, description, amount, currency, type
            FROM statements
            WHERE account_number = :accountNumber
            """)
                .bind("accountNumber", accountNumber)
                .map(new StatementMapper())
                .list();

        return new Account(accountNumber, statements);
    }
}
