package com.daphne.zincworks.atm.contoller.v1;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import com.daphne.zincworks.atm.exception.InsufficientBalanceException;
import com.daphne.zincworks.atm.service.ATMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@Slf4j
public class ATMController {
    private final ATMService service;

    ATMController(ATMService service) {
        this.service = service;
    }

    @GetMapping("/users")
    List<UserDTO> getAllUsers() {
        return service.findUsers();
    }


    @GetMapping("/users/balance")
    AccountDTO getBalance(@RequestParam Integer pin) {
        log.info(String.valueOf(pin));
        try {
            return service.getBalance(pin);
        } catch (AccessDeniedException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to dispense funds. " + exc.getMessage());
        }
    }

    @PutMapping("/users/withdraw")
    ResponseEntity<AccountDTO> getBalance(@RequestParam Integer pin, @RequestParam Integer amount) {
        try {
            AccountDTO accountDTO = service.putWithdrawal(pin, amount);
            return new ResponseEntity<>(accountDTO, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect Pin. " + exc.getMessage());
        } catch (InsufficientBalanceException exc) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Unable to dispense funds. " + exc.getMessage());
        }
    }
}