package com.epam.task.Spring_boot_task.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeListDto {
    private String username;
    private String firstName;
    private String lastName;

    public TraineeListDto() {

    }
}
