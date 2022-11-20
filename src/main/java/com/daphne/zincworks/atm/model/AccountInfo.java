package com.daphne.zincworks.atm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountInfo {

    private int accountNumber;
    private int openingBalance;
    private int overdraft;
    private Map<Integer, Integer> notesToDispense;
    private int maxWithdrawal;

}
