package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import com.daphne.zincworks.atm.exception.BadRequestException;
import com.daphne.zincworks.atm.exception.InsufficientBalanceException;
import com.daphne.zincworks.atm.model.AccountInfo;
import com.daphne.zincworks.atm.repo.AccountRepository;
import com.daphne.zincworks.atm.repo.BankRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Slf4j
@Service
public class ATMServiceImpl implements ATMService {

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    ATMServiceImpl(AccountRepository accountRepository, BankRepository bankRepository) {
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Override
    public AccountDTO verifyUser(Integer pin) throws AccessDeniedException {
        AccountDTO accountDTO = accountRepository.findByPin(pin);
        if (accountDTO == null) {
            throw new AccessDeniedException(null, null, "pin incorrect");
        }
        return accountDTO;
    }

    @Override
    public AccountInfo getBalance(Integer pin) throws AccessDeniedException {
        AccountDTO accountDTO = verifyUser(pin);
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber(accountDTO.getAccountNumber());
        accountInfo.setOpeningBalance(accountDTO.getOpeningBalance());
        accountInfo.setOverdraft(accountDTO.getOverdraft());
        accountInfo.setMaxWithdrawal(accountDTO.getOpeningBalance() + accountDTO.getOverdraft());

        return accountInfo;
    }

    @Override
    public AccountInfo withdrawal(Integer pin, Integer withdrawalAmount) throws AccessDeniedException {
        AccountDTO userAccount = verifyUser(pin);

        if (withdrawalAmount > availableUserFunds(userAccount.getOpeningBalance(), userAccount.getOverdraft())) {
            throw new InsufficientBalanceException("User does not have the sufficient funds for this request");
        }
        if (withdrawalAmount > availableBankFunds(withdrawalAmount)) {
            throw new InsufficientBalanceException("Bank does not have the sufficient funds for this request");
        }
        updateBalance(userAccount, withdrawalAmount);
        Map<Integer, Integer> dispensedNotes = notesToDependence(withdrawalAmount);
        accountRepository.save(userAccount);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountNumber(userAccount.getAccountNumber());
        accountInfo.setOpeningBalance(userAccount.getOpeningBalance());
        accountInfo.setOverdraft(userAccount.getOverdraft());
        accountInfo.setNotesToDispense(dispensedNotes);
        accountInfo.setMaxWithdrawal(userAccount.getOpeningBalance() + userAccount.getOverdraft());

        return accountInfo;
    }

    private int availableUserFunds(int open, int overdraft) {
        return open + overdraft;
    }

    private int availableBankFunds(Integer withdrawalAmount) {

        List<NotesDTO> noteAmounts = bankRepository.findAll();
        Map<Integer, Integer> notes = noteAmounts.get(0).getNoteAmount();

        Integer smallestDenomination = Collections.min(notes.keySet());
        if (withdrawalAmount % smallestDenomination > 0) {
            throw new BadRequestException("Withdrawal requests should be in multiples of " + smallestDenomination);
        }

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
        else if (account.getOpeningBalance() + account.getOverdraft() >= withdrawalAmount) {
            int remaining = Math.abs(account.getOpeningBalance() - withdrawalAmount);
            int openingBalance = account.getOpeningBalance();
            account.setOpeningBalance(0);
            account.setOverdraft(account.getOverdraft() - remaining);
            log.debug("withdrawing {} from opening balance and {} from overdraft", openingBalance, remaining);
        } else {
            throw new InsufficientBalanceException("User does not have the sufficient funds for this request");
        }
    }

    private Map<Integer, Integer> notesToDependence(Integer withdrawalAmount) {

        Map<Integer, Integer> dispense = new HashMap<>();
        NotesDTO notesDTO = bankRepository.findAll().get(0);

        Map<Integer, Integer> bankNotesLimit = notesDTO.getNoteAmount();
        //Add banknotes to TreeMap to allow sorting
        Map<Integer, Integer> notesInBank = new TreeMap<>((Collections.reverseOrder()));
        notesInBank.putAll(bankNotesLimit);

//       while there is still remaining withdrawal amount, loop through notesInBank and subtract
//       note amount from withdrawal amount, starting from the largest denomination.
        while (withdrawalAmount > 0) {
            for (Map.Entry<Integer, Integer> bank : notesInBank.entrySet()) {
                int numOfNotes = 0;
                while (bank.getValue() > 0 && withdrawalAmount > 0 && withdrawalAmount >= bank.getKey()) {
                    withdrawalAmount = withdrawalAmount - bank.getKey();
                    bank.setValue(bank.getValue() - 1);
                    numOfNotes++;

                    bankNotesLimit.put(bank.getKey(), bank.getValue());
                }
                dispense.put(bank.getKey(), numOfNotes);
            }
        }
        // update banknote amounts in database
        notesDTO.setNoteAmount(bankNotesLimit);
        bankRepository.save(notesDTO);
        return dispense;
    }
}
