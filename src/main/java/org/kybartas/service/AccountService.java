package org.kybartas.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    public BigDecimal getBalance(String accountNumber) {
        return BigDecimal.ZERO;
    }

}
