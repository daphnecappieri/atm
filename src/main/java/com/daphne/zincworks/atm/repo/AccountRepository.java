package com.daphne.zincworks.atm.repo;

import com.daphne.zincworks.atm.dto.AccountDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountDTO, Long> {
    AccountDTO findByPin(Integer pin);
}
