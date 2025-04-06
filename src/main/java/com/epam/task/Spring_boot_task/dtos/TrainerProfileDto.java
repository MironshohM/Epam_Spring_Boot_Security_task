package com.epam.task.Spring_boot_task.dtos;

import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileDto {
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeListDto> trainees;
    private String username;

    public TrainerProfileDto(String username, String firstName, String lastName, String specialization, boolean active, List<TraineeListDto> collect) {
    }

    public TrainerProfileDto() {

    }
}