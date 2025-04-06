package com.epam.task.Spring_boot_task.service;

import com.epam.task.Spring_boot_task.converter.TraineeConverter;
import com.epam.task.Spring_boot_task.converter.TrainerConverter;
import com.epam.task.Spring_boot_task.dtos.TrainerDto;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.repository.TraineeRepository;
import com.epam.task.Spring_boot_task.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private Generator generator;

    @Mock
    private TrainerConverter converter;

    @InjectMocks
    private TrainerService trainerService;

    private TrainerDto trainerDto;
    private Trainer trainer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        // Prepare test data for trainerDto
        trainerDto = new TrainerDto();
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setSpecialization("Java");

        // Prepare the expected Trainer entity (the entity that should be saved)
        trainer = new Trainer();
        trainer.setUsername("john.doe1");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setSpecialization("Java");
    }

    @Test
    public void testSaveTrainerSuccess() {
        // Mock the behavior of external methods
        when(trainerRepository.getExistingUserCount("John", "Doe")).thenReturn(0);
        when(generator.generateUsername("John", "Doe", 0)).thenReturn("john.doe1");
        when(generator.generatePassword()).thenReturn("password123");
        when(converter.trainerDtoToTrainer(trainerDto, "john.doe1", "password123")).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Call the service method
        Trainer savedTrainer = trainerService.saveTrainer(trainerDto);

        // Verify the interactions and the result
        verify(trainerRepository).getExistingUserCount("John", "Doe");
        verify(generator).generateUsername("John", "Doe", 0);
        verify(generator).generatePassword();
        verify(converter).trainerDtoToTrainer(trainerDto, "john.doe1", "password123");
        verify(trainerRepository).save(trainer);

        // Assert the saved trainer details
        assertEquals("john.doe1", savedTrainer.getUsername());
        assertEquals("John", savedTrainer.getFirstName());
        assertEquals("Doe", savedTrainer.getLastName());
        assertEquals("Java", savedTrainer.getSpecialization());
    }

    @Test
    public void testSaveTrainerWithInvalidData() {
        // Prepare an invalid TrainerDto with missing first name
        TrainerDto invalidTrainerDto = new TrainerDto();
        invalidTrainerDto.setFirstName(null); // Invalid data (first name is required)
        invalidTrainerDto.setLastName("Doe");
        invalidTrainerDto.setSpecialization("Java");

        // Call the service method and expect it to throw a validation exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.saveTrainer(invalidTrainerDto);
        });

        // Assert that the exception message is correct (you can customize this validation)
        assertTrue(exception.getMessage().contains("First name is required"));
    }

    @Test
    public void testSaveTrainerWithExistingUsername() {
        // Prepare the DTO with valid data but username conflict
        when(trainerRepository.getExistingUserCount("John", "Doe")).thenReturn(1); // Simulate existing user

        // Mock the generator behavior to generate a username
        when(generator.generateUsername("John", "Doe", 1)).thenReturn("john.doe2");
        when(generator.generatePassword()).thenReturn("password123");
        when(converter.trainerDtoToTrainer(trainerDto, "john.doe2", "password123")).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        // Call the service method
        Trainer savedTrainer = trainerService.saveTrainer(trainerDto);

        // Verify the interactions
        verify(trainerRepository).getExistingUserCount("John", "Doe");
        verify(generator).generateUsername("John", "Doe", 1);
        verify(generator).generatePassword();
        verify(converter).trainerDtoToTrainer(trainerDto, "john.doe2", "password123");
        verify(trainerRepository).save(trainer);

        // Assert the saved trainer details (should have username "john.doe2")
        assertEquals("john.doe2", savedTrainer.getUsername());
    }



    @Test
    public void testUpdateTrainerSuccess() {
        // Mock the behavior of the repository update method
        when(trainerRepository.update(1L, "John", "Doe", true, "Java")).thenReturn(1);

        // Call the service method
        boolean result = trainerService.updateTrainer(trainer);

        // Verify the repository interaction
        verify(trainerRepository).update(1L, "John", "Doe", true, "Java");

        // Assert that the result is true (update was successful)
        assertTrue(result);
    }

    @Test
    public void testUpdateTrainerWithMissingFields() {
        // Prepare trainer with missing first name
        trainer.setFirstName(null);

        // Call the service method and expect it to throw an IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            trainerService.updateTrainer(trainer);
        });

        // Assert that the exception message is correct
        assertTrue(exception.getMessage().contains("All required fields must be provided"));
    }

    @Test
    public void testUpdateTrainerUnsuccessful() {
        // Mock the behavior of the repository update method to return 0 (no rows updated)
        when(trainerRepository.update(1L, "John", "Doe", true, "Java")).thenReturn(0);

        // Call the service method
        boolean result = trainerService.updateTrainer(trainer);

        // Verify the repository interaction
        verify(trainerRepository).update(1L, "John", "Doe", true, "Java");

        // Assert that the result is false (update was unsuccessful)
        assertFalse(result);
    }


}