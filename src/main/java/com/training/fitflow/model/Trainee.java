package com.training.fitflow.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class Trainee extends User {
    public Trainee(Long id, String firstName, String lastName,
                   String username, String password, Boolean isActive,
                   LocalDate dateOfBirth, String address) {
        super(id, firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }
    private LocalDate dateOfBirth;
    private String address;
}
