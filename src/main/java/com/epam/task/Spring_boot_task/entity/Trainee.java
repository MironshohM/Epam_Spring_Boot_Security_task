package com.epam.task.Spring_boot_task.entity;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "trainees")
public class Trainee extends User {

    @Column(nullable = false)
    private String trainingProgram;

    @Column(name = "date_of_birth", nullable = true)
    private LocalDate dateOfBirth;

    @Column(name = "address", nullable = true)
    private String address;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers = new ArrayList<>();




    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();


    public Trainee() {

    }

    public Trainee(String trainingProgram, LocalDate dateOfBirth, List<Trainer> trainers, Set<Training> trainings) {
        this.trainingProgram = trainingProgram;
        this.dateOfBirth = dateOfBirth;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String trainingProgram, LocalDate dateOfBirth, String address, List<Trainer> trainers, Set<Training> trainings) {
        this.trainingProgram = trainingProgram;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, String trainingProgram, LocalDate dateOfBirth, String address, List<Trainer> trainers, Set<Training> trainings) {
        super(firstName, lastName, username, password, isActive);
        this.trainingProgram = trainingProgram;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String trainingProgram, List<Trainer> trainers, Set<Training> trainings, LocalDate dateOfBirth) {
        this.trainingProgram = trainingProgram;
        this.trainers = trainers;
        this.trainings = trainings;
        this.dateOfBirth=dateOfBirth;
    }

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, String trainingProgram, List<Trainer> trainers, Set<Training> trainings) {
        super(firstName, lastName, username, password, isActive);
        this.trainingProgram = trainingProgram;
        this.trainers = trainers;
        this.trainings = trainings;
    }

    public Trainee(String username, String password) {
        super(username, password);
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }




    public Set<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Set<Training> trainings) {
        this.trainings = trainings;
    }


    // Getters and Setters
    public String getTrainingProgram() {
        return trainingProgram;
    }

    public void setTrainingProgram(String trainingProgram) {
        this.trainingProgram = trainingProgram;
    }

    public List<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<Trainer> trainers) {
        this.trainers = trainers;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
