package com.epam.task.Spring_boot_task.repository;



import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.entity.Training;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee,Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Trainee t SET t.firstName = :#{#trainee.firstName}, t.lastName = :#{#trainee.lastName}, t.dateOfBirth = :#{#trainee.dateOfBirth}, t.address = :#{#trainee.address}, t.isActive = :#{#trainee.isActive} WHERE t.id = :#{#trainee.id}")
    int update(@Param("trainee") Trainee trainee);

    @Transactional
    @Modifying
    @Query("DELETE FROM Trainee t WHERE t.id = :id")
    int delete(@Param("id") Long id);

    Optional<Trainee> findTraineeByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE Trainee t SET t.password = :newPassword WHERE t.username = :username AND t.password = :oldPassword")
    int updatePassword(@Param("username") String username,
                       @Param("oldPassword") String oldPassword,
                       @Param("newPassword") String newPassword);


    @Transactional
    @Modifying
    @Query("UPDATE Trainee t SET t.isActive = :active WHERE t.username = :username")
    int updateActivationStatus(@Param("username") String username, @Param("active") boolean active);

    @Transactional
    boolean deleteTraineeByUsername(String username);

    Optional<Trainee> findByUsernameAndPassword(String username, String password);



    @Query("SELECT COUNT(t) FROM Trainee t WHERE t.firstName = :firstName AND t.lastName = :lastName")
    int getExistingUserCount(@Param("firstName") String firstName,
                             @Param("lastName") String lastName);


    @Query("SELECT t FROM Training t JOIN t.trainee tr " +
            "WHERE tr.username = :traineeUsername " +
            "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
            "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
            "AND (:trainerName IS NULL OR t.trainer.username = :trainerName) " +
            "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)")
    List<Training> getTraineeTrainings(@Param("traineeUsername") String traineeUsername,
                                       @Param("fromDate") LocalDate fromDate,
                                       @Param("toDate") LocalDate toDate,
                                       @Param("trainerName") String trainerName,
                                       @Param("trainingType") String trainingType);



}
