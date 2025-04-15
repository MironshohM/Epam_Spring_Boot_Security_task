package com.epam.task.Spring_boot_task.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerProfileDto {
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeListDto> trainees;
    private String username;

    public TrainerProfileDto(String username, String firstName, String lastName, String specialization, boolean active, List<TraineeListDto> collect) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.isActive = active;
        this.trainees = collect;

    }

}