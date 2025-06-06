package org.kybartas;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.kybartas.statement.Statement;
import org.kybartas.account.Account;
import org.kybartas.account.AccountRepository;
import org.kybartas.statement.StatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    private static final int NUM_OF_ACCOUNTS = 5;
    private static final int NUM_OF_STATEMENTS_FOR_ACCOUNT = 50;
    private static final int TOTAL_STATEMENTS = NUM_OF_ACCOUNTS * NUM_OF_STATEMENTS_FOR_ACCOUNT;

    @TempDir
    static Path tempDir;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13-alpine")
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Test
    @Order(1)
    void testStatementController_generateCSV() {

        try {
            this.mockMvc.perform(get("/api/statements/generateCSV").
                            param("numberOfAccounts", String.valueOf(NUM_OF_ACCOUNTS)).
                            param("transactionsPerAccount", String.valueOf(NUM_OF_STATEMENTS_FOR_ACCOUNT)).
                            param("directory", tempDir.toString())).
                    andExpect(status().isOk());

        } catch (Exception e) {
            fail("Failed to generate CSV statements : " + e.getMessage());
        }

        List<Path> csvFiles = getGeneratedCSVFilePaths();
        assertEquals(5, csvFiles.size(), "Should have " + NUM_OF_ACCOUNTS + " generated CSV files");
    }

    @Test
    @Order(2)
    void testStatementController_importCSV() {

        List<Path> csvFiles = getGeneratedCSVFilePaths();

        for(Path path : csvFiles) {
            try {
                byte[] content = Files.readAllBytes(path);
                MockMultipartFile mockFile = new MockMultipartFile(
                        "file",
                        path.getFileName().toString(),
                        "text/csv",
                        content
                );

                this.mockMvc.perform(multipart("/api/statements/import").
                                file(mockFile)).
                        andExpect(status().isOk());

            } catch (Exception e) {
                fail("Failed to read " + path + ": " + e.getMessage());
            }
        }

        List<Statement> importedStatements = statementRepository.findAll();
        assertEquals(TOTAL_STATEMENTS, importedStatements.size(), "Should be " + TOTAL_STATEMENTS + " imported statements");
        validateStatements(importedStatements);

        List<Account> accounts = accountRepository.findAll();
        assertEquals(NUM_OF_ACCOUNTS, accounts.size(), "Should be " + NUM_OF_ACCOUNTS + " imported accounts");
        validateAccounts(accounts);
    }

    @Test
    @Order(3)
    void testStatementController_exportCSV() {

        for(Account account : accountRepository.findAll()) {

            MvcResult exportResult = null;
            try {
                exportResult = this.mockMvc.perform(get("/api/statements/export").
                                param("accountNumber", account.getAccountNumber())).
                        andExpect(status().isOk()).
                        andReturn();
            } catch (Exception e) {
                fail("Failed to export statements for account number " + account.getAccountNumber() + ": " + e.getMessage());
            }

            String csvString = null;
            try {
                csvString = exportResult.getResponse().getContentAsString();
            } catch (UnsupportedEncodingException e) {
                fail("getContentAsString failed: " + e.getMessage());
            }

            List<Statement> exportedStatements = parseCsvStringToStatementList(csvString);
            assertEquals(NUM_OF_STATEMENTS_FOR_ACCOUNT, exportedStatements.size(),
                    "Should be " + NUM_OF_STATEMENTS_FOR_ACCOUNT + " exported statements for account " + account.getAccountNumber());
            validateStatements(exportedStatements);
        }
    }

    @Test
    @Order(4)
    void testAccountController_getBalance() {

        for(Account account : accountRepository.findAll()) {
             try {
                 assertNotNull(this.mockMvc.perform(get("/api/accounts/" + account.getAccountNumber())));
             } catch (Exception e) {
                 fail("Failed to get account balance for account " + account.getAccountNumber() + ": " + e.getMessage());
             }
        }
    }

    List<Path> getGeneratedCSVFilePaths() {
        try (Stream<Path> files = Files.list(tempDir)) {
            return files.filter(path -> path.toString().endsWith(".csv")).toList();
        } catch (Exception e) {
            fail("Failed to list files in generated directory: " + e.getMessage());
        }
        return null;
    }

    void validateStatements(List<Statement> statements) {
        for(Statement statement : statements) {
            assertNotNull(statement.getAccountNumber(), "statement should have account number");
            assertNotNull(statement.getAmount(), "statement should have amount");
            assertNotNull(statement.getType(), "statement should have type");
            assertNotNull(statement.getBeneficiary(), "statement should have beneficiary");
            assertNotNull(statement.getDate(), "statement should have date");
            assertNotNull(statement.getCurrency(), "statement should have currency");
        }
    }

    void validateAccounts(List<Account> accounts) {
        for(Account account : accounts) {
            assertNotNull(account.getAccountNumber(), "account should have account number");
            assertNotNull(account.getBalance(), "account should have balance");

            BigDecimal balance = BigDecimal.ZERO;
            List<Statement> statements = statementRepository.findByAccountNumber(account.getAccountNumber());
            for(Statement statement : statements) {
                if(statement.getType().equals("K")) {
                    balance = balance.add(statement.getAmount());
                } else {
                    balance = balance.subtract(statement.getAmount());
                }
            }
            assertEquals(balance, account.getBalance(), "account balance is not calculated correctly");
        }
    }

    private List<Statement> parseCsvStringToStatementList (String csvString) {
        String[] lines = csvString.split("\n");

        List<Statement> statements = new ArrayList<>();
        for(String line : lines) {
            String[] parts = line.split(",");
            Statement statement = new Statement();
            statement.setAccountNumber(parts[0].replace("\"", ""));
            statement.setDate(LocalDate.parse(parts[1].replace("\"", "")));
            statement.setBeneficiary(parts[2].replace("\"", ""));
            statement.setAmount(new BigDecimal(parts[4].replace("\"", "")));
            statement.setCurrency(parts[5].replace("\"", ""));
            statement.setType(parts[6].replace("\"", ""));
            statements.add(statement);
        }
        return statements;
    }
}