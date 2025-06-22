package org.kybartas.account.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("""
           SELECT t FROM Transaction t
           WHERE t.accountNumber = :accountNumber
           ORDER BY t.date DESC
           """)
    List<Transaction> getTransactionsByAccountNumber(String accountNumber);

    List<Transaction> findTop20ByAccountNumberOrderByDateDesc(String accountNumber);

    @Query("""
           SELECT t FROM Transaction t
           WHERE t.accountNumber = :accountNumber
           AND t.date >= :from
           AND t.date <= :to
           ORDER BY t.date DESC
           """)
    List<Transaction> getTransactionsByAccountNumberAndDateRange(String accountNumber, LocalDate from, LocalDate to);

    @Query(value = """
            SELECT SUM(
            CASE
            WHEN type = 'K' THEN amount
            WHEN type = 'D' THEN -amount ELSE 0 END)
            FROM transaction
            WHERE account_number = :accountNumber
            AND date >= :from
            AND date <= :to
            """, nativeQuery = true)
    BigDecimal calculateAccountBalanceByDateRange(String accountNumber, LocalDate from, LocalDate to);
}
