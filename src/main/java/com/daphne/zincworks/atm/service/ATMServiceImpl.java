package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import com.daphne.zincworks.atm.repo.AccountRepository;
import com.daphne.zincworks.atm.repo.BankRepository;
import com.daphne.zincworks.atm.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public UserDTO findUser(String productId) {
        return null;
    }

    @Override
    public List<UserDTO> findUsers() {
        return userRepository.findAll();
    }

    @Override
    public AccountDTO verifyUser(Integer pin) {
        return accountRepository.findByPin(pin);
    }

    @Override
    public AccountDTO getBalance(Integer pin) {
        AccountDTO account = verifyUser(pin);
        return account;
    }

    @Override
    public AccountDTO putWithdrawal(Integer pin, Integer withdrawalAmount) {
        AccountDTO account = verifyUser(pin);

        if (withdrawalAmount > availableUserFunds(account.getOpeningBalance(), account.getOverdraft())) {
            log.error("user does not have funds");
        }
        if (withdrawalAmount > availableBankFunds()) {
            log.error("bank does not have funds");
        }
        newBalance(account, withdrawalAmount);
        accountRepository.save(account);

        Map<Integer, Integer> notesToDispense = notesAmount(withdrawalAmount);

        System.out.println(notesToDispense);
        return account;
    }

    private int availableUserFunds(int open, int overdraft) {
        return open + overdraft;
    }

    private int availableBankFunds() {

        List<NotesDTO> noteAmounts = bankRepository.findAll();
        Map<Integer, Integer> notes = noteAmounts.get(0).getNoteAmount();

        List<Integer> bankTotal = notes.entrySet().stream()
                .map(e -> e.getValue() * e.getKey()).toList();

        Integer sum = bankTotal.stream().mapToInt(Integer::intValue).sum();
        System.out.println(sum);
        return sum;
    }

    private void newBalance(AccountDTO account, Integer withdrawalAmount) {
        log.info("newBalance");

//      if there is enough money in OpeningBalance, withdraw.
        if (account.getOpeningBalance() >= withdrawalAmount) {
            account.setOpeningBalance(account.getOpeningBalance() - withdrawalAmount);
            log.info("enough in balance");
        }

//      if not enough in OpeningBalance, withdraw remainder from overdraft.
        else if (account.getOverdraft() >= withdrawalAmount) {
            int remaining = Math.abs(account.getOpeningBalance() - withdrawalAmount);
            account.setOpeningBalance(0);
            account.setOverdraft(account.getOverdraft() - remaining);
        } else {
            System.out.println("Error not enough");
            log.info("Error not enough");
        }
        System.out.println(account);

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

