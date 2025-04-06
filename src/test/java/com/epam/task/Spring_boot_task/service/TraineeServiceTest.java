package com.epam.task.Spring_boot_task.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.task.Spring_boot_task.converter.TraineeConverter;
import com.epam.task.Spring_boot_task.dtos.TraineeDto;
import com.epam.task.Spring_boot_task.dtos.TraineeProfileDto;
import com.epam.task.Spring_boot_task.dtos.TraineeUpdateDto;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.exceptions.EntityNotFoundException;
import com.epam.task.Spring_boot_task.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private Generator generator;

    @Mock
    private TraineeConverter converter;

    @InjectMocks
    private TraineeService traineeService;

    private TraineeDto traineeDto;
    private Trainee trainee;
    private TraineeUpdateDto traineeUpdateDto;

    private TraineeProfileDto traineeProfileDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        // Prepare test data
        traineeDto = new TraineeDto();
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");

        traineeUpdateDto = new TraineeUpdateDto();
        traineeUpdateDto.setUsername("john.doe");
        traineeUpdateDto.setFirstName("John");
        traineeUpdateDto.setLastName("Doe");

        trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("OldFirstName");
        trainee.setLastName("OldLastName");

        traineeProfileDto = new TraineeProfileDto();
        traineeProfileDto.setUsername("john.doe");
        traineeProfileDto.setFirstName("John");
        traineeProfileDto.setLastName("Doe");
    }

    @Test
    public void testSaveTraineeSuccess() {
        // Mock the behavior of the generator and repository
        when(traineeRepository.getExistingUserCount("John", "Doe")).thenReturn(0); // No existing trainees with same name
        when(generator.generateUsername("John", "Doe", 0)).thenReturn("john.doe");
        when(generator.generatePassword()).thenReturn("generatedPassword123");
        when(converter.traineeDtoToTrainee(traineeDto, "john.doe", "generatedPassword123")).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        // Call the service method
        Trainee savedTrainee = traineeService.saveTrainee(traineeDto);

        // Verify the interactions and the result
        verify(traineeRepository).save(trainee); // Ensure save was called
        assertEquals("john.doe", savedTrainee.getUsername()); // Check the username
        assertEquals("generatedPassword123", savedTrainee.getPassword()); // Check the password
    }

    @Test
    public void testSaveTraineeMissingFirstName() {
        // Prepare a trainee DTO with missing first name
        TraineeDto invalidDto = new TraineeDto();
        invalidDto.setFirstName(null);  // Missing first name
        invalidDto.setLastName("Doe");

        // Verify that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.saveTrainee(invalidDto);
        });
        assertEquals("First name and last name are required!", exception.getMessage());
    }

    @Test
    public void testSaveTraineeMissingLastName() {
        // Prepare a trainee DTO with missing last name
        TraineeDto invalidDto = new TraineeDto();
        invalidDto.setFirstName("John");
        invalidDto.setLastName(null);  // Missing last name

        // Verify that an IllegalArgumentException is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.saveTrainee(invalidDto);
        });
        assertEquals("First name and last name are required!", exception.getMessage());
    }
    @Test
    public void testUpdateTraineeProfileSuccess() {
        // Mock the behavior of repository and converter
        when(traineeRepository.findTraineeByUsername("john.doe")).thenReturn(Optional.of(trainee));
        when(converter.TraineeUpdateDtoToTrainee(traineeUpdateDto, trainee)).thenReturn(trainee);
        when(traineeRepository.save(trainee)).thenReturn(trainee);
        when(converter.traineeToTraineeProfileDto(trainee)).thenReturn(traineeProfileDto);

        // Call the service method
        TraineeProfileDto updatedProfile = traineeService.updateTraineeProfile(traineeUpdateDto);

        // Verify the interactions and the result
        verify(traineeRepository).findTraineeByUsername("john.doe"); // Ensure find method was called
        verify(traineeRepository).save(trainee); // Ensure save was called
        assertEquals("john.doe", updatedProfile.getUsername()); // Verify the profile details
        assertEquals("John", updatedProfile.getFirstName());
        assertEquals("Doe", updatedProfile.getLastName());
    }

    @Test
    public void testUpdateTraineeProfileNotFound() {
        // Mock the repository to return an empty optional (trainee not found)
        when(traineeRepository.findTraineeByUsername("john.doe")).thenReturn(Optional.empty());

        // Call the service method and verify that it throws EntityNotFoundException
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            traineeService.updateTraineeProfile(traineeUpdateDto);
        });

        // Assert the exception message
        assertEquals("Trainee not found with username: john.doe", exception.getMessage());
    }

    @Test
    public void testUpdateTraineeProfileInvalidUpdate() {
        // Prepare an invalid update DTO with null first name
        TraineeUpdateDto invalidTraineeDto = new TraineeUpdateDto();
        invalidTraineeDto.setUsername("john.doe");
        invalidTraineeDto.setFirstName(null); // Invalid data
        invalidTraineeDto.setLastName("Doe");

        // Mock the repository to return an existing trainee
        when(traineeRepository.findTraineeByUsername("john.doe")).thenReturn(Optional.of(trainee));

        // Call the service method and expect it to throw an IllegalArgumentException or similar
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            traineeService.updateTraineeProfile(invalidTraineeDto);
        });

        // Assert that the exception message is correct (You may customize this validation)
        assertTrue(exception.getMessage().contains("Invalid input"));
    }
}


