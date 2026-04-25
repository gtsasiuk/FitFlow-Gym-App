package com.training.fitflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "trainees")
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

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    @Column(name = "address")
    private String address;

    @Override
    public String toString() {
        return "Trainee{" +
                " id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active + '\'' +
                ", dateOfBirth=" + dateOfBirth + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
