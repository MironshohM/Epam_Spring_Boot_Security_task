package com.epam.task.Spring_boot_task.service;



import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainer;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TrainerService {
    boolean login(String username, String password);

    void logout();

    Trainer saveTrainer(TrainerDto Trainer);

    boolean updateTrainer(Trainer Trainer);

    boolean deleteTrainer(Long id);

    Optional<Trainer> findTrainerById(Long id);

    List<Trainer> findAllTrainers();

    TrainerProfileDto findTrainerProfileByUsername(String username) ;

    boolean updatePassword(String username, String oldPassword, String newPassword);

    void updateTrainerStatus(String username, boolean isActive);

    TrainerProfileDto updateTrainerProfile(TrainerUpdateDto updateDto);

    List<TrainerDto> getUnassignedTrainers(String traineeUsername);

    List<TrainerTrainingResponseDto> getTrainerTrainings(TrainerTrainingRequestDto request);

    ResponseEntity<MonthlySummaryDTO> getMonthlySummary(String username, int year, int month);
}
