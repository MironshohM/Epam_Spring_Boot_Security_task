package com.epam.task.Spring_boot_task.service;


import com.epam.task.Spring_boot_task.converter.TraineeConverter;
import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.exceptions.EntityNotFoundException;
import com.epam.task.Spring_boot_task.exceptions.UnauthorizedAccessException;
import com.epam.task.Spring_boot_task.repository.TraineeRepository;
import com.epam.task.Spring_boot_task.repository.TrainerRepository;
import com.epam.task.Spring_boot_task.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("dev")
public class TraineeServiceImpl implements TraineeService {

    private static boolean signed = false;
    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;

    private final Generator generator;

    private final TrainerRepository trainerRepository;

    private final TraineeConverter converter;

    private final UserRepository userRepository;

    @Autowired
    public TraineeServiceImpl(TraineeRepository traineeRepository, Generator generator, TrainerRepository trainerRepository, TraineeConverter converter, UserRepository userRepository) {
        this.traineeRepository = traineeRepository;
        this.generator = generator;
        this.trainerRepository = trainerRepository;
        this.converter = converter;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String username, String password) {
        logger.info("Attempting login for username: {}", username);
        if(traineeRepository.findByUsernameAndPassword(username, password).isPresent()){
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
    public Trainee saveTrainee(TraineeDto traineeDto) {
        logger.info("Attempting to save trainee: {}", traineeDto);

        // Validate required fields
        if (traineeDto.getFirstName() == null || traineeDto.getLastName() == null) {
            throw new IllegalArgumentException("First name and last name are required!");
        }
        // Generate username & password for new trainees
        int serialNumber = traineeRepository.getExistingUserCount(traineeDto.getFirstName(), traineeDto.getLastName());
        String username = generator.generateUsername(traineeDto.getFirstName(), traineeDto.getLastName(), serialNumber);
        String password = generator.generatePassword();

        // Convert DTO to Entity
        Trainee trainee = converter.traineeDtoToTrainee(traineeDto,username,password);

        logger.info("Trainee's username and password generated: {}", trainee.getUsername());

        // Save Trainee and check the result
        return traineeRepository.save(trainee);

    }


    public TraineeProfileDto updateTraineeProfile(TraineeUpdateDto traineeDto) {
        Trainee trainee = traineeRepository.findTraineeByUsername(traineeDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found with username: " + traineeDto.getUsername()));

        logger.info("Updating trainee profile for username: {}", traineeDto.getUsername());
        // Update fields
        trainee=converter.TraineeUpdateDtoToTrainee(traineeDto,trainee);
        // Save updated trainee
        traineeRepository.save(trainee);
        return converter.traineeToTraineeProfileDto(trainee);
    }

    @Override
    public boolean deleteTrainee(Long id) {
//        if (!signed) {
//            logger.warn("Unauthorized delete attempt. User not logged in.");
//            return false;
//        }
        logger.info("Attempting to delete trainee with ID: {}", id);
        int deletedRows=traineeRepository.delete(id);
        if(deletedRows>0){
            logger.info("Trainee deleted successfully for user: {}", id);
            return true;
        }else {
            logger.warn("Failed to delete Trainee: {}", id);
            return false;
        }

    }

    @Override
    public Optional<Trainee> findTraineeById(Long id) {
        logger.info("Finding for Trainee with ID: {}", id);
        return traineeRepository.findById(id);
    }

    @Override
    public List<Trainee> findAllTrainees() {
        logger.info("Fetching all  Trainees");
        return traineeRepository.findAll();
    }

    @Override
    public TraineeProfileDto findTraineeByUsername(String username) {
        logger.info("Searching for Trainee with username: {}", username);

        Trainee trainee = traineeRepository.findTraineeByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found with username: " + username));

        // Convert Trainee entity -> TraineeProfileDto
        return converter.traineeToTraineeProfileDto(trainee);
    }


    @Override
    public boolean updateTraineePassword(String username, String oldPassword, String newPassword) {
//        if (!signed) { // Authentication check
//            logger.warn("Unauthorized access attempt! Please log in.");
//            throw new UnauthorizedAccessException("Unauthorized access attempt! Please log in.");
//        }

        int updatedRows = traineeRepository.updatePassword(username, oldPassword, newPassword);
        if (updatedRows > 0) {
            logger.info("Password updated successfully for user: {}", username);
            return true;
        } else {
            logger.warn("Failed to update password: Incorrect username or old password for {}", username);
            return false;
        }
    }

    @Override
    public void updateTraineeStatus(String username, boolean isActive) {
//        if (!signed) {
//            throw new UnauthorizedAccessException("Unauthorized access attempt! Please log in.");
//        }

        int updatedRows = traineeRepository.updateActivationStatus(username, isActive);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Trainee not found with username: " + username);
        }
        logger.info("Updated activation status for user: {} to {}", username, isActive);
    }
    @Override
    public void deleteTraineeByUsername(String username) {
//        if (!signed) {
//            logger.warn("Unauthorized access attempt! Please log in.");
//            throw new UnauthorizedAccessException("Unauthorized access! Please log in.");
//        }

        int deletedCount = traineeRepository.deleteTraineeByUsername(username);

        if (deletedCount ==0) {
            throw new EntityNotFoundException("Trainee with username: " + username + " not found.");
        }
    }

    @Override
    public List<TrainingDto> getTraineeTrainings(TraineeTrainingRequestDto request) {
//        if (!signed) {
//            throw new UnauthorizedAccessException("Unauthorized access attempt! Please log in.");
//        }

        // Validate date range
        if (request.getPeriodFrom().isAfter(request.getPeriodTo())) {
            throw new IllegalArgumentException("Invalid date range: periodFrom must be before periodTo.");
        }

        List<Training> trainings = traineeRepository.getTraineeTrainings(
                request.getUsername(), request.getPeriodFrom(),
                request.getPeriodTo(), request.getTrainerName(), request.getTrainingType()
        );

        return converter.trainingsToTrainingDto(trainings);
    }


    @Override
    public List<TrainerDto> updateTraineeTrainerList(String traineeUsername, List<String> trainerUsernames) {
        logger.info("Updating trainers for trainee: {}", traineeUsername);

        // Find the trainee, throw an exception if not found
        Trainee trainee = traineeRepository.findTraineeByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found with username: " + traineeUsername));

        // Retrieve trainers from DB
        List<Trainer> trainers = trainerRepository.findByUsernames(trainerUsernames);

        if (trainers.isEmpty()) {
            throw new EntityNotFoundException("No valid trainers found for given usernames: " + trainerUsernames);
        }

        // Set trainers to trainee
        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);

        // Convert to DTO and return
        return converter.trainersToTrainerDtos(trainers);
    }


}
