package com.daphne.zincworks.atm.service;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import com.daphne.zincworks.atm.exception.BadRequestException;
import com.daphne.zincworks.atm.exception.InsufficientBalanceException;
import com.daphne.zincworks.atm.model.AccountInfo;
import com.daphne.zincworks.atm.repo.AccountRepository;
import com.daphne.zincworks.atm.repo.BankRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class ATMServiceImplTest {

    private AccountRepository mockAccountRepository;
    private ATMService atmService;
    private BankRepository mockBankRepository;

    @BeforeEach
    void setup() {
        mockAccountRepository = Mockito.mock(AccountRepository.class);
        mockBankRepository = Mockito.mock(BankRepository.class);
        atmService = new ATMServiceImpl(mockAccountRepository, mockBankRepository);
    }

    @Test
    void verifyUserTest() throws AccessDeniedException {
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());
        atmService.verifyUser(1234);
        Mockito.verify(mockAccountRepository, Mockito.times(1))
                .findByPin(1234);
    }

    @Test
    void verifyUserTestThrowsAccessDeniedException() {
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> {
            atmService.verifyUser(1234);
        });
    }

    @Test
    void getBalanceTest() throws AccessDeniedException {
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());
        AccountInfo accountInfo =atmService.getBalance(1234);
        Mockito.verify(mockAccountRepository, Mockito.times(1))
                .findByPin(1234);

        Assertions.assertEquals(800, accountInfo.getOpeningBalance());
        Assertions.assertEquals(200, accountInfo.getOverdraft());
        Assertions.assertEquals(1000, accountInfo.getMaxWithdrawal());
        Assertions.assertEquals(123456789, accountInfo.getAccountNumber());
    }

    @Test
    void verifyGetBalanceThrowsExceptionWhenPinIncorrect() {
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> {
            atmService.getBalance(1234);
        });
    }

    @Test
    void putWithdrawalTest() throws AccessDeniedException {
        NotesDTO notesDTO = createBank();
        List<NotesDTO> notesDTOList = new ArrayList<>();
        notesDTOList.add(notesDTO);
        Mockito.when(mockBankRepository.findAll()).thenReturn(notesDTOList);
        Mockito.when(mockBankRepository.save(any())).thenReturn(notesDTO);
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());

        AccountInfo accountInfo = atmService.withdrawal(1234, 185);

        ArgumentCaptor<NotesDTO> argument = ArgumentCaptor.forClass(NotesDTO.class);
        Mockito.verify(mockAccountRepository, Mockito.times(1))
                .findByPin(1234);
        Mockito.verify(mockBankRepository, Mockito.times(1))
                .save(argument.capture());
        Mockito.verify(mockBankRepository, Mockito.times(2))
                .findAll();

        NotesDTO noteAmounts = argument.getValue();
        Map<Integer, Integer> notes = noteAmounts.getNoteAmount();
        Assertions.assertEquals(7, notes.get(50));
        Assertions.assertEquals(29, notes.get(20));
        Assertions.assertEquals(29, notes.get(10));
        Assertions.assertEquals(19, notes.get(5));

        Assertions.assertEquals(615, accountInfo.getOpeningBalance());
        Assertions.assertEquals(200, accountInfo.getOverdraft());
        Assertions.assertEquals(815, accountInfo.getMaxWithdrawal());
        Assertions.assertEquals(123456789, accountInfo.getAccountNumber());
    }

    @Test
    void putWithdrawalTest_overdraft() throws AccessDeniedException {
        NotesDTO notesDTO = createBank();
        List<NotesDTO> notesDTOList = new ArrayList<>();
        notesDTOList.add(notesDTO);
        Mockito.when(mockBankRepository.findAll()).thenReturn(notesDTOList);
        Mockito.when(mockBankRepository.save(any())).thenReturn(notesDTO);
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());

        AccountInfo accountInfo = atmService.withdrawal(1234, 945);

        ArgumentCaptor<NotesDTO> argument = ArgumentCaptor.forClass(NotesDTO.class);
        Mockito.verify(mockAccountRepository, Mockito.times(1))
                .findByPin(1234);
        Mockito.verify(mockBankRepository, Mockito.times(1))
                .save(argument.capture());
        Mockito.verify(mockBankRepository, Mockito.times(2))
                .findAll();

        NotesDTO noteAmounts = argument.getValue();
        Map<Integer, Integer> notes = noteAmounts.getNoteAmount();
        Assertions.assertEquals(0, notes.get(50));
        Assertions.assertEquals(8, notes.get(20));
        Assertions.assertEquals(30, notes.get(10));
        Assertions.assertEquals(19, notes.get(5));

        Assertions.assertEquals(0, accountInfo.getOpeningBalance());
        Assertions.assertEquals(55, accountInfo.getOverdraft());
        Assertions.assertEquals(55, accountInfo.getMaxWithdrawal());
        Assertions.assertEquals(123456789, accountInfo.getAccountNumber());
    }

    @Test
    void withdrawalThrowsExceptionWhenUserHasInsufficientFunds() {
        NotesDTO notesDTO = createBank();
        List<NotesDTO> notesDTOList = new ArrayList<>();
        notesDTOList.add(notesDTO);
        Mockito.when(mockBankRepository.findAll()).thenReturn(notesDTOList);
        Mockito.when(mockBankRepository.save(any())).thenReturn(notesDTO);
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());

        assertThrows(InsufficientBalanceException.class, () -> {
            atmService.withdrawal(1234, 1020);
        });
    }

    @Test
    void withdrawalThrowsExceptionWhenBankHasInsufficientFunds(){
        NotesDTO notesDTO = createBank();
        List<NotesDTO> notesDTOList = new ArrayList<>();
        notesDTOList.add(notesDTO);
        Mockito.when(mockBankRepository.findAll()).thenReturn(notesDTOList);
        Mockito.when(mockBankRepository.save(any())).thenReturn(notesDTO);
        AccountDTO accountDTO = createUserAccount();
        accountDTO.setOpeningBalance(2000);
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(accountDTO);

        assertThrows(InsufficientBalanceException.class, () -> {
            atmService.withdrawal(1234, 1600);
        });
    }

    @Test
    void withdrawalThrowsExceptionWhenIncorrectMultiples() {
        NotesDTO notesDTO = createBank();
        List<NotesDTO> notesDTOList = new ArrayList<>();
        notesDTOList.add(notesDTO);
        Mockito.when(mockBankRepository.findAll()).thenReturn(notesDTOList);
        Mockito.when(mockBankRepository.save(any())).thenReturn(notesDTO);
        Mockito.when(mockAccountRepository.findByPin(any())).thenReturn(createUserAccount());

        assertThrows(BadRequestException.class, () -> {
            atmService.withdrawal(1234, 133);
        });
    }

    private AccountDTO createUserAccount() {
        AccountDTO userAccount = new AccountDTO();
        userAccount.setOpeningBalance(800);
        userAccount.setOverdraft(200);
        userAccount.setAccountNumber(123456789);
        userAccount.setPin(1234);

        return userAccount;
    }

    private NotesDTO createBank() {
        NotesDTO notesDTO = new NotesDTO();
        Map<Integer, Integer> noteAmount = new TreeMap<>();
        noteAmount.put(50, 10);
        noteAmount.put(20, 30);
        noteAmount.put(10, 30);
        noteAmount.put(5, 20);
        notesDTO.setNoteAmount(noteAmount);
        return notesDTO;
    }

}