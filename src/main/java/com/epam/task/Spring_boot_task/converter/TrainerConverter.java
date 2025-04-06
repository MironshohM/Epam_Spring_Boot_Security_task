package com.epam.task.Spring_boot_task.converter;


import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("dev")
public class TrainerConverter {

    public Trainer trainerDtoToTrainer(TrainerDto trainerDto,String username,String password){
        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerDto.getFirstName());
        trainer.setLastName(trainerDto.getLastName());
        trainer.setSpecialization(trainerDto.getSpecialization());
        // Generate unique username & password

        trainer.setUsername(username);
        trainer.setPassword(password);
        return trainer;
    }

    public TrainerProfileDto trainerToTrainerProfileDto(Trainer trainer){
        List<TraineeListDto> traineeDtos = trainer.getTrainees().stream()
                .map(trainee -> new TraineeListDto(trainee.getUsername(), trainee.getFirstName(), trainee.getLastName()))
                .collect(Collectors.toList());

        return new TrainerProfileDto(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization(),
                trainer.isActive(),
                traineeDtos
        );
    }

    public Trainer trainerProfileDtoToTrainer(TrainerUpdateDto updateDto, Trainer trainer){
        trainer.setFirstName(updateDto.getFirstName());
        trainer.setLastName(updateDto.getLastName());
        trainer.setActive(updateDto.getIsActive()); // Update active status
        return trainer;
    }

    public List<TrainerTrainingResponseDto> trainingsToTrainerTrainingResponseDto(List<Training> trainings){
        return trainings.stream()
                .map(training -> new TrainerTrainingResponseDto(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainee().getFirstName() + " " + training.getTrainee().getLastName()
                ))
                .collect(Collectors.toList());
    }

    public List<TrainerDto> trainersToTrainerDtos(List<Trainer> unassignedTrainers){
        return unassignedTrainers.stream()
                .filter(Trainer::isActive)
                .map(trainer -> new TrainerDto(
                        trainer.getUsername(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization()
                ))
                .collect(Collectors.toList());
    }


}
