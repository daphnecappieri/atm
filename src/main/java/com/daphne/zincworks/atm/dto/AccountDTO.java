package com.daphne.zincworks.atm.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class AccountDTO {

    private
    @Id
    @GeneratedValue
//    @Column(name="Account Number")
    int accountNumber;
//    @Column(name="PIN")
    private int pin;
//    @Column(name="Opening Balance")
    private int openingBalance;
//    @Column(name="Overdraft")

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

