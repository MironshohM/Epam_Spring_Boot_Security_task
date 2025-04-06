package com.epam.task.Spring_boot_task.service;



import com.epam.task.Spring_boot_task.dtos.TrainingTypeDto;
import com.epam.task.Spring_boot_task.entity.Training;

import java.util.List;

public interface TrainingService {

    boolean addTraining(String traineeUsername, String trainerUsername, Training training);

    List<TrainingTypeDto> getAllTrainingTypes();

}
