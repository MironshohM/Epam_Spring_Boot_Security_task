package com.epam.task.Spring_boot_task.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TrainerDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String username;

    public TrainerDto(String username, String firstName, String lastName, String specialization) {
    }

    public TrainerDto() {

    }

}
