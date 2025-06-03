package com.epam.task.Spring_boot_task.service;


import com.epam.task.Spring_boot_task.converter.TrainerConverter;
import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.feign.TrainerWorkloadService;
import com.epam.task.Spring_boot_task.repository.TrainerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("dev")
public class TrainerServiceImpl implements TrainerService {

    private final TrainerWorkloadService trainerWorkloadService;
    private final TrainerConverter converter;
    private static boolean signed = false;
    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;

    private final Generator generator;

    @Autowired
    public TrainerServiceImpl(TrainerWorkloadService trainerWorkloadService, TrainerConverter converter, TrainerRepository trainerRepository, Generator generator) {
        this.trainerWorkloadService = trainerWorkloadService;
        this.converter = converter;
        this.trainerRepository = trainerRepository;
        this.generator = generator;
    }

    @Override
    public boolean login(String username, String password) {
        logger.info("Attempting login for username: {}", username);
        if(trainerRepository.findByUsernameAndPassword(username,password).isPresent()){
            signed=true;
        }
        return signed;
    }

    @Override
    public void logout() {
        if (!signed) {
            logger.warn("Logout attempt when no user is signed in.");
            return;
        }

        signed = false;
        logger.info("User logged out successfully.");
    }

    @Override
    public Trainer saveTrainer(TrainerDto trainerDto) {
        logger.info("Attempting to save trainer: {}", trainerDto);

        int serialNumber = trainerRepository.getExistingUserCount(trainerDto.getFirstName(), trainerDto.getLastName());
        String username = generator.generateUsername(trainerDto.getFirstName(), trainerDto.getLastName(), serialNumber);
        String password = generator.generatePassword();
        // Convert DTO to Entity
        Trainer trainer = converter.trainerDtoToTrainer(trainerDto,username,password);
        logger.info("Trainer's username and password generated: {}", trainer.getUsername());
        // Save and return Trainer
        return trainerRepository.save(trainer);
    }

    @Override
    public boolean updateTrainer(Trainer trainer) {
        logger.info("Attempting to update Trainer with ID: {}", trainer.getId());
        if (trainer.getFirstName() == null || trainer.getLastName() == null ||
                trainer.getSpecialization() == null) {
            throw new IllegalArgumentException("All required fields must be provided!");
        }
        int updated=trainerRepository.update(trainer.getId(),trainer.getFirstName(),trainer.getLastName(),trainer.isActive(),trainer.getSpecialization());
        return updated > 0;
    }

    @Override
    public boolean deleteTrainer(Long id) {

        logger.info("Attempting to delete Trainer with ID: {}", id);

        int rowsDeleted = trainerRepository.delete(id);

        return rowsDeleted > 0;
    }

    @Override
    public Optional<Trainer> findTrainerById(Long id) {
        logger.info("Finding for Trainer with ID: {}", id);
        return trainerRepository.findTrainerById(id);
    }

    @Override
    public List<Trainer> findAllTrainers() {
        logger.info("Fetching all  Trainers");
        return trainerRepository.findAll();
    }

    @Override
    public TrainerProfileDto findTrainerProfileByUsername(String username) {
        Trainer trainer = trainerRepository.findTrainerByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with username: " + username));

        return converter.trainerToTrainerProfileDto(trainer);
    }


    @Override
    public TrainerProfileDto updateTrainerProfile(TrainerUpdateDto updateDto) {
        Trainer trainer = trainerRepository.findTrainerByUsername(updateDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with username: " + updateDto.getUsername()));


        trainer=converter.trainerProfileDtoToTrainer(updateDto,trainer);
        // Save and return updated trainer
        trainer = trainerRepository.save(trainer);

        return converter.trainerToTrainerProfileDto(trainer);
    }

    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword) {

        int updatedCount = trainerRepository.updatePassword(username, oldPassword, newPassword);

        if (updatedCount > 0) {
            logger.info("Password updated successfully for username: {}", username);
            return true;
        } else {
            logger.warn("Invalid username or old password for updating password: {}", username);
            return false;
        }
    }


    @Override
    public void updateTrainerStatus(String username, boolean isActive) {

        int rowsUpdated = trainerRepository.updateActivationStatus(username, isActive);

        if (rowsUpdated == 0) {
            throw new EntityNotFoundException("Trainer not found with username: " + username);
        }
    }



    @Override
    public List<TrainerTrainingResponseDto> getTrainerTrainings(TrainerTrainingRequestDto request) {
        // Validate date range
        if (request.getPeriodFrom().isAfter(request.getPeriodTo())) {
            throw new IllegalArgumentException("Invalid date range: periodFrom must be before periodTo.");
        }


        List<Training> trainings = trainerRepository.getTrainerTrainings(
                request.getUsername(), request.getPeriodFrom(),
                request.getPeriodTo(), request.getTraineeName()
        );

       return converter.trainingsToTrainerTrainingResponseDto(trainings);
    }

    @Override
    public List<TrainerDto> getUnassignedTrainers(String traineeUsername) {

        // Check if trainee exists
        boolean traineeExists = trainerRepository.existsByUsername(traineeUsername);
        if (!traineeExists) {
            throw new EntityNotFoundException("Trainee not found with username: " + traineeUsername);
        }



        // Filter only active trainers and convert to DTO
        List<Trainer> unassignedTrainers = trainerRepository.getUnassignedTrainers(traineeUsername);
        return converter.trainersToTrainerDtos(unassignedTrainers);

    }

    @Override
    public ResponseEntity<MonthlySummaryDTO> getMonthlySummary(String username, int year, int month) {
        logger.info("Fetching monthly training summary for: {} - {}/{}", username, month, year);

        try {
            return trainerWorkloadService.getMonthlySummary(username, year, month);
        } catch (Exception e) {
            logger.error("Error fetching summary from summary microservice", e);
            throw new RuntimeException("Unable to fetch summary from workload service");
        }
    }

}
