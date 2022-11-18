package com.daphne.zincworks.atm.repo;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Configuration
@Slf4j
public class RepoConfiguration {


    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, AccountRepository accountRepository, BankRepository bankRepository) {

        return args -> {
            log.info("loading testing data: user" + userRepository.save(new UserDTO("Daphne", "burglar")));
            log.info("loading testing data: user " + userRepository.save(new UserDTO("Frodo Baggins", "thief")));

            log.info("loading testing data: account" + accountRepository.save(new AccountDTO(123456789, 1234, 800, 200)));
            log.info("loading testing data: account " + accountRepository.save(new AccountDTO(987654321, 4321, 1230, 150)));

            Map<Integer, Integer> noteAmount = new TreeMap<>();
            noteAmount.put( 50, 10);
            noteAmount.put(20,30);
            noteAmount.put(10,30);
            noteAmount.put(5,20);
            log.info("loading testing data: bankNotes" + bankRepository.save(new NotesDTO(noteAmount)));
        };
    }
}