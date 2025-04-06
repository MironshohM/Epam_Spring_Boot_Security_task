package com.epam.task.Spring_boot_task.converter;

import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class TraineeConverter {


    public Trainee traineeDtoToTrainee(TraineeDto traineeDto,String username,String password){
        Trainee trainee=new Trainee();
        trainee.setFirstName(traineeDto.getFirstName());
        trainee.setLastName(traineeDto.getLastName());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress());
        trainee.setUsername(username);
        trainee.setPassword(password);
        return trainee;
    }

    public Trainee TraineeUpdateDtoToTrainee(TraineeUpdateDto traineeDto,Trainee trainee){
        trainee.setFirstName(traineeDto.getFirstName());
        trainee.setLastName(traineeDto.getLastName());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress());
        trainee.setActive(traineeDto.isActive());
        return trainee;
    }

    public TraineeProfileDto traineeToTraineeProfileDto(Trainee trainee){
        // Convert to TraineeProfileDto for response
        TraineeProfileDto profileDto = new TraineeProfileDto();
        profileDto.setUsername(trainee.getUsername());
        profileDto.setFirstName(trainee.getFirstName());
        profileDto.setLastName(trainee.getLastName());
        profileDto.setDateOfBirth(trainee.getDateOfBirth());
        profileDto.setAddress(trainee.getAddress());
        profileDto.setActive(trainee.isActive());

        // Convert Trainers List
        List<TrainerDto> trainers = trainee.getTrainers().stream()
                .map(trainer -> new TrainerDto(trainer.getUsername(), trainer.getFirstName(),
                        trainer.getLastName(), trainer.getSpecialization()))
                .collect(Collectors.toList());

        profileDto.setTrainers(trainers);
        return profileDto;
    }

    public List<TrainingDto> trainingsToTrainingDto(List<Training> trainings){
        return trainings.stream()
                .map(training -> new TrainingDto(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainer().getFirstName() + " " + training.getTrainer().getLastName()
                ))
                .collect(Collectors.toList());
    }

    public List<TrainerDto> trainersToTrainerDtos(List<Trainer> trainers){
        return trainers.stream()
                .map(trainer -> new TrainerDto(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization()
                ))
                .collect(Collectors.toList());
    }
}
