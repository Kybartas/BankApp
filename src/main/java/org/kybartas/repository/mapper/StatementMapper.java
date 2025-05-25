package org.kybartas.repository.mapper;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.kybartas.entity.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatementMapper implements RowMapper<Statement> {

    @Override
    public Statement map (ResultSet rs, StatementContext ctx) throws SQLException {

        return new Statement(
                rs.getString("account_number"),
                rs.getDate("date").toLocalDate(),
                rs.getString("beneficiary"),
                rs.getString("description"),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                rs.getString("type")
        );
    }
}
