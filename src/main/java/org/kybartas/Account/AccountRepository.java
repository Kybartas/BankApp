package org.kybartas.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AccountRepository extends JpaRepository<Account, String> {

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
    BigDecimal getBalanceByDates(String accountNumber, LocalDate from, LocalDate to);

}
