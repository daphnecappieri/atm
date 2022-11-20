package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.model.AccountInfo;

import java.nio.file.AccessDeniedException;

public interface ATMService {

    /**
     * Verify users pin
     *
     * @return AccountDTO containing users account info
     */
    AccountDTO verifyUser(Integer pin) throws AccessDeniedException;

    /**
     * Get users account balance
     *
     * @return AccountInfo containing users account info
     */
    AccountInfo getBalance(Integer pin) throws AccessDeniedException;

    /**
     * Update users account by withdrawing from balance
     *
     * @return AccountDTO containing users account info
     */
    AccountInfo withdrawal(Integer pin, Integer amount) throws AccessDeniedException;
}
