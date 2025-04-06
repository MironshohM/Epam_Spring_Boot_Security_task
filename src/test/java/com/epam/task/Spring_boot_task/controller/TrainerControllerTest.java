package com.epam.task.Spring_boot_task.controller;

import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.service.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(TrainerController.class)
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testRegisterTrainer_Success() throws Exception {
        // Given a valid TrainerDto with trainer information
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("trainer1");
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setSpecialization("Java Developer");

        // Mock saved trainer response from the service layer
        Trainer savedTrainer = new Trainer();
        savedTrainer.setUsername("trainer1");
        savedTrainer.setFirstName("John");
        savedTrainer.setLastName("Doe");
        savedTrainer.setSpecialization("Java Developer");
        savedTrainer.setPassword("securePassword123");

        UserDto expectedUserDto = new UserDto(savedTrainer.getUsername(), savedTrainer.getPassword());

        // Mock service response
        when(trainerService.saveTrainer(any(TrainerDto.class))).thenReturn(savedTrainer);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isCreated()) // Assert HTTP 201 status code
                .andExpect(jsonPath("$.username").value("trainer1"))
                .andExpect(jsonPath("$.password").value("securePassword123"));
    }

    @Test
    void testRegisterTrainer_BadRequest_InvalidData() throws Exception {
        // Given an invalid TrainerDto with missing required fields (e.g., username is missing)
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setSpecialization("Java Developer");

        // Perform the POST request and assert the response (400 Bad Request)
        mockMvc.perform(post("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(jsonPath("$.message").value("Username is required")); // Assumed validation error message
    }

    @Test
    void testRegisterTrainer_InternalServerError() throws Exception {
        // Given a valid TrainerDto
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setUsername("trainer1");
        trainerDto.setFirstName("John");
        trainerDto.setLastName("Doe");
        trainerDto.setSpecialization("Java Developer");

        // Mock service to throw an exception indicating an internal server error
        when(trainerService.saveTrainer(any(TrainerDto.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Perform the POST request and assert the response (500 Internal Server Error)
        mockMvc.perform(post("/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerDto)))
                .andExpect(status().isInternalServerError()) // Assert HTTP 500 status code
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void testLoginTrainer_Success() throws Exception {
        // Given a valid UserDto with correct credentials
        UserDto userDto = new UserDto("trainer1", "securePassword123");

        // Mock the service response to return true, indicating successful authentication
        when(trainerService.login(userDto.getUsername(), userDto.getPassword())).thenReturn(true);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk()) // Assert HTTP 200 status code
                .andExpect(content().string("Successfully logged in"));
    }

    @Test
    void testLoginTrainer_InvalidCredentials() throws Exception {
        // Given a UserDto with invalid credentials
        UserDto userDto = new UserDto("trainer1", "wrongPassword");

        // Mock the service response to return false, indicating failed authentication
        when(trainerService.login(userDto.getUsername(), userDto.getPassword())).thenReturn(false);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized()) // Assert HTTP 401 status code
                .andExpect(jsonPath("$.message").value("Username or password incorrect")); // Assumed error message
    }

    @Test
    void testLoginTrainer_InternalServerError() throws Exception {
        // Given a valid UserDto with valid credentials
        UserDto userDto = new UserDto("trainer1", "securePassword123");

        // Mock the service to throw an exception indicating an internal server error
        when(trainerService.login(userDto.getUsername(), userDto.getPassword()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Perform the POST request and assert the response (500 Internal Server Error)
        mockMvc.perform(post("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError()) // Assert HTTP 500 status code
                .andExpect(jsonPath("$.message").value("Internal server error")); // Assumed error message
    }

    @Test
    void testChangeTrainerPassword_Success() throws Exception {
        // Given a valid UpdateUserDto with correct username and passwords
        UpdateUserDto userDto = new UpdateUserDto("trainer1", "oldPassword123", "newPassword123");

        // Mock the service response to return true, indicating successful password update
        when(trainerService.updatePassword(userDto.getUsername(), userDto.getOldPassword(), userDto.getNewPassword()))
                .thenReturn(true);

        // Perform the PUT request and assert the response
        mockMvc.perform(put("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk()) // Assert HTTP 200 status code
                .andExpect(content().string("Successfully changed password"));
    }

    @Test
    void testChangeTrainerPassword_InvalidOldPassword() throws Exception {
        // Given an UpdateUserDto with invalid old password
        UpdateUserDto userDto = new UpdateUserDto("trainer1", "wrongOldPassword", "newPassword123");

        // Mock the service response to return false, indicating that the old password is incorrect
        when(trainerService.updatePassword(userDto.getUsername(), userDto.getOldPassword(), userDto.getNewPassword()))
                .thenReturn(false);

        // Perform the PUT request and assert the response
        mockMvc.perform(put("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(content().string("Username or old password incorrect"));
    }

    @Test
    void testChangeTrainerPassword_InternalServerError() throws Exception {
        // Given a valid UpdateUserDto
        UpdateUserDto userDto = new UpdateUserDto("trainer1", "oldPassword123", "newPassword123");

        // Mock the service to throw an exception indicating an internal server error
        when(trainerService.updatePassword(userDto.getUsername(), userDto.getOldPassword(), userDto.getNewPassword()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Perform the PUT request and assert the response (500 Internal Server Error)
        mockMvc.perform(put("/trainer/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isInternalServerError()) // Assert HTTP 500 status code
                .andExpect(jsonPath("$.message").value("Internal server error")); // Assumed error message
    }

    @Test
    void testGetTrainerProfile_Success() throws Exception {
        // Given a valid username and a corresponding trainer profile with trainees
        String username = "trainer1";
        List<TraineeListDto> trainees = Arrays.asList(
                new TraineeListDto("trainee1", "John", "Doe"),
                new TraineeListDto("trainee2", "Jane", "Doe")
        );
        TrainerProfileDto trainerProfileDto = new TrainerProfileDto(username, "John", "Doe", "Java", true, trainees);

        // Mock the service response to return a valid trainer profile
        when(trainerService.findTrainerProfileByUsername(username)).thenReturn(trainerProfileDto);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/trainer/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Assert HTTP 200 status code
                .andExpect(jsonPath("$.username").value("trainer1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.specialization").value("Java"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees[0].username").value("trainee1"))
                .andExpect(jsonPath("$.trainees[0].firstName").value("John"))
                .andExpect(jsonPath("$.trainees[0].lastName").value("Doe"))
                .andExpect(jsonPath("$.trainees[1].username").value("trainee2"))
                .andExpect(jsonPath("$.trainees[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.trainees[1].lastName").value("Doe"));
    }

    @Test
    void testGetTrainerProfile_TrainerNotFound() throws Exception {
        // Given a username that does not exist
        String username = "nonExistentTrainer";

        // Mock the service response to return null or throw an exception if not found
        when(trainerService.findTrainerProfileByUsername(username)).thenReturn(null);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/trainer/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Assert HTTP 404 status code
                .andExpect(jsonPath("$.message").value("Trainer not found"));
    }

    @Test
    void testGetTrainerProfile_InternalServerError() throws Exception {
        // Given a valid username
        String username = "trainer1";

        // Mock the service to throw an exception indicating an internal server error
        when(trainerService.findTrainerProfileByUsername(username))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Perform the GET request and assert the response (500 Internal Server Error)
        mockMvc.perform(get("/trainer/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Assert HTTP 500 status code
                .andExpect(jsonPath("$.message").value("Internal server error")); // Assumed error message
    }





}
