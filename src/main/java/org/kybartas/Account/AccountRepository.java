package org.kybartas.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Query("""
           SELECT a FROM Account a WHERE a.accountNumber = :accountNumber
           """)
    Optional<Account> findAccountByAccountNumber(String accountNumber);

    @Query(value = """
            SELECT * FROM account
            ORDER BY account_number
            """, nativeQuery = true)
    List<Account> getAllAccountsOrdered();
}