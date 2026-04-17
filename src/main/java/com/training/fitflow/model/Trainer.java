package com.training.fitflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {
    public Trainer(Long id, String firstName, String lastName,
                   String username, String password, Boolean isActive,
                   String specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }
    private String specialization;
}
