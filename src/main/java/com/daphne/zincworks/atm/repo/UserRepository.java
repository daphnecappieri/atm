package com.daphne.zincworks.atm.repo;

import com.daphne.zincworks.atm.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDTO, Long> {


}
