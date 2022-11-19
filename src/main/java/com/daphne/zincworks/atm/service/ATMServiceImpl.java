package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import com.daphne.zincworks.atm.exception.InsufficientBalanceException;
import com.daphne.zincworks.atm.model.AccountInfo;
import com.daphne.zincworks.atm.repo.AccountRepository;
import com.daphne.zincworks.atm.repo.BankRepository;
import com.daphne.zincworks.atm.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Service
public class ATMServiceImpl implements ATMService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    ATMServiceImpl(UserRepository userRepository, AccountRepository accountRepository, BankRepository bankRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public List<UserDTO> findUsers() {
        return userRepository.findAll();
    }

    @Override
    public AccountDTO verifyUser(Integer pin) throws AccessDeniedException {
        AccountDTO accountDTO = accountRepository.findByPin(pin);
        if (accountDTO == null) {
            throw new AccessDeniedException(null, null, "pin incorrect");
        }
//        AccountInfo accountInfo = new AccountInfo();
//        accountInfo.setAccountNumber(accountDTO.getAccountNumber());
//        accountInfo.setOpeningBalance(accountDTO.setOpeningBalance();
//        accountInfo.setOverdraft(accountDTO.getOverdraft());

        return accountDTO;
    }

    @Override
    public AccountInfo getBalance(Integer pin) throws AccessDeniedException {
        AccountDTO accountDTO = verifyUser(pin);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber(accountDTO.getAccountNumber());
        accountInfo.setOpeningBalance(accountDTO.getAccountNumber());
        accountInfo.setOverdraft(accountDTO.getOverdraft());
        return accountInfo;
    }

    @Override
    public AccountInfo putWithdrawal(Integer pin, Integer withdrawalAmount) throws AccessDeniedException {
        AccountDTO account = verifyUser(pin);

        if (withdrawalAmount > availableUserFunds(account.getOpeningBalance(), account.getOverdraft())) {
            throw new InsufficientBalanceException("User does not have the sufficient funds for this request");
        }
        if (withdrawalAmount > availableBankFunds()) {
            throw new InsufficientBalanceException("Bank does not have the sufficient funds for this request");

        }
        updateBalance(account, withdrawalAmount);
        accountRepository.save(account);
        Map<Integer, Integer> notesToDispense = notesAmount(withdrawalAmount);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber(account.getAccountNumber());
        accountInfo.setOpeningBalance(account.getOpeningBalance());
        accountInfo.setOverdraft(account.getOverdraft());
        accountInfo.setNotesToDispense(notesToDispense);
        log.debug("user account: {} ", accountInfo);

        return accountInfo;
    }

    private int availableUserFunds(int open, int overdraft) {
        return open + overdraft;
    }

    private int availableBankFunds() {

        List<NotesDTO> noteAmounts = bankRepository.findAll();
        Map<Integer, Integer> notes = noteAmounts.get(0).getNoteAmount();

        List<Integer> bankTotal = notes.entrySet().stream()
                .map(e -> e.getValue() * e.getKey()).toList();
        return bankTotal.stream().mapToInt(Integer::intValue).sum();
    }

    private void updateBalance(AccountDTO account, Integer withdrawalAmount) {

//      if there is enough money in users OpeningBalance, withdraw.
        if (account.getOpeningBalance() >= withdrawalAmount) {
            account.setOpeningBalance(account.getOpeningBalance() - withdrawalAmount);
            log.debug("withdrawing {} from opening balance", withdrawalAmount);
        }

//      if not enough in OpeningBalance, withdraw remainder from overdraft.
        else if (account.getOverdraft() >= withdrawalAmount) {
            int remaining = Math.abs(account.getOpeningBalance() - withdrawalAmount);
            int openingBalance = account.getOpeningBalance();
            account.setOpeningBalance(0);
            account.setOverdraft(account.getOverdraft() - remaining);
            log.debug("withdrawing {} from opening balance and {} from overdraft", openingBalance, remaining);
        } else {
            throw new InsufficientBalanceException("User does not have the sufficient funds for this request");

        }
    }

    private Map<Integer, Integer> notesAmount(Integer withdrawalAmount) {

        Map<Integer, Integer> dispense = new HashMap<>();
        List<NotesDTO> noteAmounts = bankRepository.findAll();
        NotesDTO notesDTO = noteAmounts.get(0);
        Map<Integer, Integer> bankNotesLimit = notesDTO.getNoteAmount();
        Map<Integer, Integer> notesInBank = new TreeMap<>((Collections.reverseOrder()));
        notesInBank.putAll(bankNotesLimit);

        while (withdrawalAmount > 0) {
            for (Map.Entry<Integer, Integer> bank : notesInBank.entrySet()) {
                int numOfNotes = 0;
                while (bank.getValue() > 0 && withdrawalAmount > 0 && withdrawalAmount >= bank.getKey()) {
                    withdrawalAmount = withdrawalAmount - bank.getKey();
                    bank.setValue(bank.getValue() - 1);
                    numOfNotes++;
                    int key = bank.getKey();
                    int value = bank.getValue();
                    bankNotesLimit.put(key, value);
                }
                dispense.put(bank.getKey(), numOfNotes);
            }
        }
        notesDTO.setNoteAmount(bankNotesLimit);
        bankRepository.save(notesDTO);
        return dispense;
    }
}

