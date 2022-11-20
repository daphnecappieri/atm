package com.daphne.zincworks.atm.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class AccountDTO {

    private @Id
    @GeneratedValue int accountNumber;
    private int pin;
    private int openingBalance;
    private int overdraft;

    public AccountDTO() {
    }

    public AccountDTO(int accountNumber, int pin, int openingBalance, int overdraft) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.openingBalance = openingBalance;
        this.overdraft = overdraft;
    }
}

