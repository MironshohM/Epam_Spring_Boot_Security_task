package com.epam.task.Spring_boot_task.service;



import com.epam.task.Spring_boot_task.dtos.TrainingTypeDto;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.entity.TrainingType;
import com.epam.task.Spring_boot_task.exceptions.EntityNotFoundException;
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

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
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

        logger.info("Successfully added training for trainee: {} and trainer: {}", traineeUsername, trainerUsername);
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
