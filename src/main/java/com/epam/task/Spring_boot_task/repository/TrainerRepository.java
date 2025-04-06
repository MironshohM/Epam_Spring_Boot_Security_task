package com.epam.task.Spring_boot_task.repository;



import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.entity.Training;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer,Long> {



    @Transactional
    @Modifying
    @Query("UPDATE Trainer t SET t.firstName = :firstName, t.lastName = :lastName, " +
            "t.isActive = :isActive, t.specialization = :specialization WHERE t.id = :id")
    int update(@Param("id") Long id,
               @Param("firstName") String firstName,
               @Param("lastName") String lastName,
               @Param("isActive") boolean isActive,
               @Param("specialization") String specialization);

    @Transactional
    @Modifying
    @Query("DELETE FROM Trainer t WHERE t.id = :id")
    int delete(@Param("id") Long id);

    Optional<Trainer> findTrainerById(Long id);

    Optional<Trainer> findTrainerByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Trainer t SET t.password = :newPassword WHERE t.username = :username AND t.password = :oldPassword")
    int updatePassword(@Param("username") String username,
                       @Param("oldPassword") String oldPassword,
                       @Param("newPassword") String newPassword);

    @Modifying
    @Transactional
    @Query("UPDATE Trainer t SET t.isActive = :active WHERE t.username = :username")
    int updateActivationStatus(@Param("username") String username,
                               @Param("active") boolean active);

    Optional<Trainer> findByUsernameAndPassword(String username, String password);

    @Query("SELECT t FROM Trainer t WHERE t.id NOT IN " +
            "(SELECT tr.trainer.id FROM Training tr WHERE tr.trainee.username = :traineeUsername)")
    List<Trainer> getUnassignedTrainers(@Param("traineeUsername") String traineeUsername);

    @Query("SELECT t FROM Trainer t WHERE t.username IN :usernames")
    List<Trainer> findByUsernames(@Param("usernames") List<String> usernames);


    @Query("SELECT t FROM Training t " +
            "JOIN t.trainer tr " +
            "JOIN t.trainee trn " +
            "WHERE tr.username = :trainerUsername " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:traineeName IS NULL OR trn.username = :traineeName)")
    List<Training> getTrainerTrainings(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName);


    @Query("SELECT COUNT(t) FROM Trainer t WHERE t.firstName = :firstName AND t.lastName = :lastName")
    int getExistingUserCount(@Param("firstName") String firstName, @Param("lastName") String lastName);

    boolean existsByUsername(String username);

}