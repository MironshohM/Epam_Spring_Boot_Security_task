package com.epam.task.Spring_boot_task.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerTrainingRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    private LocalDate periodFrom;

    private LocalDate periodTo;

    private String traineeName;
}
