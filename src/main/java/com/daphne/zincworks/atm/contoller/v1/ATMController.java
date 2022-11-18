package com.daphne.zincworks.atm.contoller.v1;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import com.daphne.zincworks.atm.service.ATMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ATMController {
    private final ATMService service;

    ATMController(ATMService service) {
        this.service = service;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/users")
    List<UserDTO> getAllUsers() {
        return service.findUsers();
    }
    // end::get-aggregate-root[]



    @GetMapping("/users/balance")
    AccountDTO getBalance(@RequestParam Integer pin) {
        log.info(String.valueOf(pin));
        return service.getBalance(pin);
    }

    @PutMapping("/users/withdraw")
    AccountDTO getBalance( @RequestParam Integer pin,@RequestParam Integer amount) {
        return service.putWithdrawal(pin,amount);
    }

//    @PostMapping("/user")
//    UserDTO newEmployee(@RequestBody UserDTO newEmployee) {
//        return service.save(newEmployee);
//    }
//
//    // Single item
//
//    @GetMapping("/employees/{id}")
//    Employee one(@PathVariable Long id) {
//
//        return repository.findById(id)
//                .orElseThrow(() -> new EmployeeNotFoundException(id));
//    }
//
//    @PutMapping("/employees/{id}")
//    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
//
//        return repository.findById(id)
//                .map(employee -> {
//                    employee.setName(newEmployee.getName());
//                    employee.setRole(newEmployee.getRole());
//                    return repository.save(employee);
//                })
//                .orElseGet(() -> {
//                    newEmployee.setId(id);
//                    return repository.save(newEmployee);
//                });
//    }
//
//    @DeleteMapping("/employees/{id}")
//    void deleteEmployee(@PathVariable Long id) {
//        repository.deleteById(id);
//    }
}