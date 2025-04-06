package com.epam.task.Spring_boot_task.repository;



import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training,Long> {


    @Query("SELECT t FROM Training t WHERE t.trainee.username = :traineeUsername AND t.trainer.username = :trainerUsername")
    Optional<Training> findTrainingByTraineeAndTrainer(@Param("traineeUsername") String traineeUsername,
                                                       @Param("trainerUsername") String trainerUsername);

    @Query("SELECT t FROM TrainingType t")
    List<TrainingType> getAllTrainingTypes();

}
