package com.epam.task.Spring_boot_task.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeActivateDto {
    @NotBlank(message = "Username is required")
    private String username;

    private boolean isActive;
}
