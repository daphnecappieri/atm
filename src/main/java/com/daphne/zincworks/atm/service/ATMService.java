package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ATMService {

    List<UserDTO> findUsers();

    AccountDTO verifyUser(Integer pin) throws AccessDeniedException;

    AccountDTO getBalance(Integer pin) throws AccessDeniedException;

    AccountDTO putWithdrawal(Integer pin, Integer amount) throws AccessDeniedException;
}
