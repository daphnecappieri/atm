package com.daphne.zincworks.atm.contoller.v1;

import com.daphne.zincworks.atm.exception.BadRequestException;
import com.daphne.zincworks.atm.exception.InsufficientBalanceException;
import com.daphne.zincworks.atm.model.AccountInfo;
import com.daphne.zincworks.atm.service.ATMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;

@RestController
@Slf4j
public class ATMController {
    private final ATMService service;

    ATMController(ATMService service) {
        this.service = service;
    }

    /**
     * Get users account balance after verifying users pin.
     *
     * @param pin Users pin to be verified
     * @return AccountInfo containing users account info.
     */
    @GetMapping("/user/balance")
    AccountInfo getBalance(@RequestParam Integer pin) {
        log.info(String.valueOf(pin));
        try {
            return service.getBalance(pin);
        } catch (AccessDeniedException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to dispense funds. " + exc.getMessage());
        }
    }

    /**
     * Withdraw funds from a users account balance after verifying users pin.
     *
     * @param pin    Users pin to be verified
     * @param amount Amount user wishes to withdraw
     * @return AccountInfo containing users updated account info.
     */
    @PutMapping("/user/withdraw")
    ResponseEntity<AccountInfo> putWithdrawal(@RequestParam Integer pin, @RequestParam Integer amount) {
        try {
            AccountInfo accountDTO = service.withdrawal(pin, amount);
            return new ResponseEntity<>(accountDTO, HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException exc) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect Pin. " + exc.getMessage());
        } catch (InsufficientBalanceException exc) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Unable to dispense funds. " + exc.getMessage());
        } catch (BadRequestException exc) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unable to dispense funds. " + exc.getMessage());
        }
    }
}