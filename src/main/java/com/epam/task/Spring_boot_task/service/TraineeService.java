package com.epam.task.Spring_boot_task.service;



import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {

    boolean login(String username, String password);

    void logout();

    Trainee saveTrainee(TraineeDto traineeDto);

    TraineeProfileDto updateTraineeProfile(TraineeUpdateDto traineeDto);

    boolean deleteTrainee(Long id);

    Optional<Trainee> findTraineeById(Long id);

    List<Trainee> findAllTrainees();

    TraineeProfileDto findTraineeByUsername(String username);

    boolean updateTraineePassword(String username, String oldPassword, String newPassword);

    void updateTraineeStatus(String username, boolean isActive);

    void deleteTraineeByUsername(String username);

    List<TrainerDto> updateTraineeTrainerList(String traineeUsername, List<String> trainerUsernames);

    List<TrainingDto> getTraineeTrainings(TraineeTrainingRequestDto request);
}
