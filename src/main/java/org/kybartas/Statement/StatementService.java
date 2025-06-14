package org.kybartas.statement;


import org.kybartas.exception.ExportException;
import org.kybartas.exception.WriterException;
import org.kybartas.statement.csv.CSVStatementProcessor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StatementService {

    private final StatementRepository statementRepository;
    public StatementService(StatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    public byte[] exportCSVStatement(String accountNumber, LocalDate from, LocalDate to) throws ExportException{

        List<Statement> statements;
        try {
            if (from != null && to != null) {
                statements = statementRepository.findByAccountNumberAndDateRange(accountNumber, from, to);
            } else {
                statements = statementRepository.findByAccountNumber(accountNumber);
            }
            return CSVStatementProcessor.writeStatementsToByteArray(statements);

        } catch (WriterException e) {
            throw new ExportException("Failed to export statements to file: ", e);
        }
    }

    public BigDecimal calculateBalance(String accountNumber, LocalDate from, LocalDate to) {

        if(from != null && to != null) {
            return statementRepository.calculateBalanceByDates(accountNumber, from, to);
        }
        return statementRepository.calculateBalanceOverall(accountNumber);
    }

    public BigDecimal calculateBalanceByIds(List<Long> ids) {

        return statementRepository.calculateBalanceByIds(ids);
    }
}
