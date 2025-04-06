package com.epam.task.Spring_boot_task.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypeDto {
    private Long id;
    private String trainingType;
}