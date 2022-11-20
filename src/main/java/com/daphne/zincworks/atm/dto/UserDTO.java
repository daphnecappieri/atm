package com.daphne.zincworks.atm.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Data
@Entity
public class UserDTO {

    private @Id
    @GeneratedValue Long id;
    private String name;


    public UserDTO() {
    }

    public UserDTO(String name) {
        this.name = name;
    }


}

