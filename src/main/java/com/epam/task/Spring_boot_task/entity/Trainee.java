package com.epam.task.Spring_boot_task.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "trainees")
@NoArgsConstructor

public class Trainee extends User {

//        @Column(nullable = true)
//        private String trainingProgram;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = true)
    private String address;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers = new ArrayList<>();


    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();


    public Trainee(LocalDate dateOfBirth, List<Trainer> trainers, Set<Training> trainings) {

        this.dateOfBirth = dateOfBirth;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(LocalDate dateOfBirth, String address, List<Trainer> trainers, Set<Training> trainings) {

        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, LocalDate dateOfBirth, String address, List<Trainer> trainers, Set<Training> trainings) {
        super(firstName, lastName, username, password, isActive);

        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(List<Trainer> trainers, Set<Training> trainings, LocalDate dateOfBirth) {

        this.trainers = trainers;
        this.trainings = trainings;
        this.dateOfBirth = dateOfBirth;
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, List<Trainer> trainers, Set<Training> trainings) {
        super(firstName, lastName, username, password, isActive);
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String username, String password) {
        super(username, password);
    }

}
