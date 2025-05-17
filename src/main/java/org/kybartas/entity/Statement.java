package org.kybartas.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "statements")
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "account_number", nullable = false)
    private String accountNumber;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "beneficiary", nullable = false)
    private String beneficiary;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "currency", nullable = false)
    private String currency;
    @Column(name = "type", nullable = false)
    private String type;

    public Statement() {}

    public Statement(String accountNumber, LocalDate date, String beneficiary, String description,
                     BigDecimal amount, String currency, String type) {

        this.accountNumber = accountNumber;
        this.date = date;
        this.beneficiary = beneficiary;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
    }

    public void printStatement() {
        System.out.println(accountNumber + " " + date.toString() + " " + beneficiary + " " + description + " " + amount.toString() + " " + currency + " " + type);
    }

    public long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getBeneficiary() {
        return beneficiary;
    }
    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
