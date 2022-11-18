package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;

import java.util.List;

public interface ATMService {

    UserDTO findUser(String Id);

    List<UserDTO> findUsers();

    AccountDTO verifyUser(Integer pin);

    AccountDTO getBalance(Integer pin);

    AccountDTO putWithdrawal(Integer pin, Integer amount);
}
