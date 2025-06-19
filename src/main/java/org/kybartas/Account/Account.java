package org.kybartas.account;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
public class Account {

    @Id
    private String id;

    private String accountNumber;
    private BigDecimal balance = BigDecimal.ZERO;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            long randomPart = System.nanoTime();
            this.id = datePart + "-" + randomPart;
        }
    }

    public Account(){
    }
    public Account(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}