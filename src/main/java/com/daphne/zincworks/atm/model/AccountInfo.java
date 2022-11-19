package com.daphne.zincworks.atm.model;

import lombok.Data;

import java.util.Map;

@Data
public class AccountInfo {

    private int accountNumber;
    private int openingBalance;
    private int overdraft;
    private Map<Integer, Integer> notesToDispense;

}
