package org.kybartas.repository;

import org.kybartas.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface StatementRepository extends JpaRepository<Statement, Long> {

    @Query("""
           SELECT s FROM Statement s WHERE s.accountNumber = :accountNumber
           AND s.date >= :from
           AND s.date <= :to
           """)
    List<Statement> findByAccountNumberAndDateRange(String accountNumber, LocalDate from, LocalDate to);

    @Query("""
           SELECT s FROM Statement s WHERE s.accountNumber = :accountNumber
           """)
    List<Statement> findByAccountNumber(String accountNumber);

    @Query(value = """
            SELECT SUM(
            CASE
            WHEN type = 'K' THEN amount
            WHEN type = 'D' THEN -amount ELSE 0 END)
            FROM statements
            WHERE account_number = :accountNumber
            AND date >= :from
            AND date <= :to
            """, nativeQuery = true)
    BigDecimal getBalanceWithDates(String accountNumber, LocalDate from, LocalDate to);

    @Query(value = """
            SELECT SUM(
            CASE
            WHEN type = 'K' THEN amount
            WHEN type = 'D' THEN -amount ELSE 0 END)
            FROM statements
            WHERE account_number = :accountNumber
            """, nativeQuery = true)
    BigDecimal getBalanceAll(String accountNumber);

}
