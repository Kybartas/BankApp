/*
package org.kybartas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kybartas.entity.Account;
import org.kybartas.entity.Statement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest {

    private AccountService accountService;
    private Account dummyAccount1;
    private List<Statement> dummyStatements;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();

        dummyStatements = List.of(
                new Statement("111", LocalDate.of(2025,1,1), "A", "things", new BigDecimal(20), "EUR", "K"),
                new Statement("111", LocalDate.of(2025,2,2), "B", "things", new BigDecimal(20), "EUR", "K"),
                new Statement("111", LocalDate.of(2025,3,3), "C", "things", new BigDecimal(30), "EUR", "D")
        );
        dummyAccount1 = new Account("111", dummyStatements);
    }

    @Test
    void testFilterByDateRange() {
        List<Statement> result = accountService.filterStatementsByDateRange(dummyStatements, LocalDate.of(2025,1,1), LocalDate.of(2025,2,2));

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getBeneficiary());
        assertEquals("B", result.get(1).getBeneficiary());
    }

    @Test
    void testGetBalanceDateless() {
        BigDecimal result = accountService.getBalance(dummyAccount1);

        assertEquals(new BigDecimal(10), result);
    }

    @Test
    void testGetBalanceWithDateRange() {
        BigDecimal result = accountService.getBalance(dummyAccount1, LocalDate.of(2025,1,1), LocalDate.of(2025, 2 ,2));
        BigDecimal result1 = accountService.getBalance(dummyAccount1, LocalDate.of(2025,2,2), LocalDate.of(2025, 3,3));

        assertEquals(new BigDecimal(40), result);
        assertEquals(new BigDecimal(-10), result1);
    }
}
*/
