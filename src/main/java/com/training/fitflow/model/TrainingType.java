package com.training.fitflow.model;

import lombok.Getter;

public enum TrainingType {
    FITNESS("Fitness"),
    YOGA("Yoga"),
    ZUMBA("Zumba"),
    STRETCHING("Stretching"),
    RESISTANCE("Resistance");

    @Getter
    private final String name;


    TrainingType(String name) {
        this.name = name;
    }
}
