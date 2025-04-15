package com.epam.task.Spring_boot_task.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "trainers")
public class Trainer extends User {

    // Getters and Setters
    @Getter
    @Column(nullable = false)
    private String specialization;

    @ManyToMany
    @JoinTable(
            name = "trainer_trainee",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id")
    )
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = true)
    private TrainingType trainingType;

    public Trainer(String trainer1, String password) {
        super(trainer1,password);
    }

    public Trainer() {

    }




    public void addTrainee(Trainee trainee) {
        this.trainees.add(trainee);
        trainee.getTrainers().add(this);
    }

    public void removeTrainee(Trainee trainee) {
        this.trainees.remove(trainee);
        trainee.getTrainers().remove(this);
    }


}
