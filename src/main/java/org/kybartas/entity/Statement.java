package org.kybartas.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Statement {

    private String accountNumber;
    private LocalDate date;
    private String beneficiary;
    private String description;
    private BigDecimal amount;
    private String currency;
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
