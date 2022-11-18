package com.daphne.zincworks.atm.repo;

import com.daphne.zincworks.atm.dto.AccountDTO;
import com.daphne.zincworks.atm.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<AccountDTO, Long> {
    AccountDTO findByPin(Integer pin);
}
