package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import com.daphne.zincworks.atm.model.AccountInfo;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ATMService {

    List<UserDTO> findUsers();

    AccountDTO verifyUser(Integer pin) throws AccessDeniedException;

    AccountInfo getBalance(Integer pin) throws AccessDeniedException;

    AccountInfo putWithdrawal(Integer pin, Integer amount) throws AccessDeniedException;
}
