package com.epam.task.Spring_boot_task.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySummaryDTO {
    private String username;
    private String firstName;
    private String lastName;
    private boolean active;
    private int year;
    private int month;
    private int totalTrainingDuration;


}
