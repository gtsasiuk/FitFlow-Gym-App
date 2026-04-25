package com.training.fitflow.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
public class Trainer extends User {
    public Trainer(Long id, String firstName, String lastName,
                   String username, String password, Boolean isActive,
                   TrainingType specialization) {
        super(id, firstName, lastName, username, password, isActive);
        this.specialization = specialization;
    }

    @Enumerated(EnumType.STRING)
    private TrainingType specialization;

    @Override
    public String toString() {
        return "Trainer{" +
                "specialization=" + specialization.getName() +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                '}';
    }
}
