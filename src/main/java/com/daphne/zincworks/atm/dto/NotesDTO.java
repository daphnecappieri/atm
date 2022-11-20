package com.daphne.zincworks.atm.dto;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Map;


@Data
@Entity
public class NotesDTO {

    @Id
    @GeneratedValue
    private Long id;
    @ElementCollection
    private Map<Integer, Integer> noteAmount;

    public NotesDTO() {
    }

    public NotesDTO(Map<Integer, Integer> noteAmount) {
        this.noteAmount = noteAmount;
    }

}
