package com.epam.task.Spring_boot_task.service;



import com.epam.task.Spring_boot_task.dtos.TrainingSessionEventDTO;
import com.epam.task.Spring_boot_task.dtos.TrainingTypeDto;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.entity.TrainingType;
import com.epam.task.Spring_boot_task.exceptions.EntityNotFoundException;
import com.epam.task.Spring_boot_task.feign.TrainerWorkloadService;
import com.epam.task.Spring_boot_task.repository.TraineeRepository;
import com.epam.task.Spring_boot_task.repository.TrainerRepository;
import com.epam.task.Spring_boot_task.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Profile("dev")
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerWorkloadService trainerWorkloadService;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository, TrainerWorkloadService trainerWorkloadService) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @Transactional
    public boolean addTraining(String traineeUsername, String trainerUsername, Training training) {
        logger.info("Adding training for trainee: {} and trainer: {}", traineeUsername, trainerUsername);

        // Check if Trainee and Trainer exist
        Optional<Trainee> traineeOpt = traineeRepository.findTraineeByUsername(traineeUsername);
        Optional<Trainer> trainerOpt = trainerRepository.findTrainerByUsername(trainerUsername);

        if (traineeOpt.isEmpty() || trainerOpt.isEmpty()) {
            logger.warn("Failed to add training: Trainee or Trainer does not exist.");
            throw new EntityNotFoundException("Trainer or Trainee not found.");
        }

        Trainee trainee = traineeOpt.get();
        Trainer trainer = trainerOpt.get();

        // Check if a training already exists for this trainee and trainer
        Optional<Training> existingTraining = trainingRepository.findTrainingByTraineeAndTrainer(traineeUsername, trainerUsername);
        if (existingTraining.isPresent()) {
            logger.warn("Training already exists for trainee: {} and trainer: {}", traineeUsername, trainerUsername);
            return false;
        }

        // Set the trainee and trainer to the training object
        training.setTrainee(trainee);
        training.setTrainer(trainer);

        // Save the new training
        trainingRepository.save(training);

        // Feign client call to update workload
        TrainingSessionEventDTO event = new TrainingSessionEventDTO(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                "ADD"
        );

        try {
            trainerWorkloadService.updateWorkload(event);
            logger.info("Trainer workload updated successfully in summary service");
        } catch (Exception e) {
            logger.error("Failed to update trainer workload in summary service", e);
            // Optionally rethrow or suppress depending on criticality
        }

        logger.info("Successfully added training for trainee: {} and trainer: {}", traineeUsername, trainerUsername);
        return true;
    }

    @Transactional
    public boolean deleteTraining(String traineeUsername, String trainerUsername) {
        logger.info("Deleting training for trainee: {} and trainer: {}", traineeUsername, trainerUsername);

        Optional<Training> existingTraining = trainingRepository.findTrainingByTraineeAndTrainer(traineeUsername, trainerUsername);
        if (existingTraining.isEmpty()) {
            logger.warn("No training found for trainee: {} and trainer: {}", traineeUsername, trainerUsername);
            return false;
        }

        Training training = existingTraining.get();
        Trainer trainer = training.getTrainer();

        // Delete training
        trainingRepository.delete(training);
        logger.info("Training deleted from main database");

        // Notify microservice
        TrainingSessionEventDTO event = new TrainingSessionEventDTO(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration(),
                "DELETE"
        );

        try {
            trainerWorkloadService.updateWorkload(event);
            logger.info("Trainer workload updated successfully in summary service (DELETE)");
        } catch (Exception e) {
            logger.error("Failed to update trainer workload in summary service (DELETE)", e);
        }

        return true;
    }


    @Override
    public List<TrainingTypeDto> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingRepository.getAllTrainingTypes();

        if (trainingTypes == null || trainingTypes.isEmpty()) {
            throw new EntityNotFoundException("No training types found.");
        }

        return trainingTypes.stream()
                .map(t -> new TrainingTypeDto(t.getId(), t.getTrainingTypeName()))
                .collect(Collectors.toList());
    }

}
