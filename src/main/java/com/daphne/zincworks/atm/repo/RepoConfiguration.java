package com.daphne.zincworks.atm.repo;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.NotesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

@Configuration
@Slf4j
public class RepoConfiguration {


    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository, BankRepository bankRepository) {

        return args -> {

            log.debug("loading testing data: account" + accountRepository.save(new AccountDTO(123456789, 1234, 800, 200)));
            log.debug("loading testing data: account " + accountRepository.save(new AccountDTO(987654321, 4321, 1230, 150)));

            Map<Integer, Integer> noteAmount = new TreeMap<>();
            noteAmount.put(50, 10);
            noteAmount.put(20, 30);
            noteAmount.put(10, 30);
            noteAmount.put(5, 20);
            log.debug("loading testing data: bankNotes" + bankRepository.save(new NotesDTO(noteAmount)));
        };
    }
}