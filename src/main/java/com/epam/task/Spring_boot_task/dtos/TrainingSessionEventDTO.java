package com.epam.task.Spring_boot_task.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TrainingSessionEventDTO {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private LocalDate trainingDate;
    private int trainingDuration;
    private String actionType; // "ADD" or "DELETE"

    public TrainingSessionEventDTO(String username, String firstName, String lastName, boolean active, LocalDate trainingDate, int trainingDuration, String add) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
        this.actionType = add; // Assuming 'add' is the action type
    }
}
