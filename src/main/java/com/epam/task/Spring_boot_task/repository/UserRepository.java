package com.epam.task.Spring_boot_task.repository;



import com.epam.task.Spring_boot_task.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    boolean deleteByUsername(String username);

}