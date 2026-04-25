package com.training.fitflow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;
    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;
    @Column(name = "training_name", nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType type;
    @Column(name = "training_date", nullable = false)
    private LocalDate date;
    @Column(name = "training_duration", nullable = false)
    private Integer duration;

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", traineeId=" + (trainee != null ? trainee.getId() : null) +
                ", trainerId=" + (trainer != null ? trainer.getId() : null) +
                ", name='" + name + '\'' +
                ", type=" + (type != null ? type.getId() : null) +
                ", date=" + date +
                ", duration=" + duration +
                " minutes }";
    }
}
