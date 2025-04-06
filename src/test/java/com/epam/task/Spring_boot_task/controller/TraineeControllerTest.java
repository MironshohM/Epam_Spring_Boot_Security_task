package com.epam.task.Spring_boot_task.controller;

import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.exceptions.EntityNotFoundException;
import com.epam.task.Spring_boot_task.service.TraineeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@ExtendWith(MockitoExtension.class)
@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TraineeService traineeService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void testRegisterTrainee() throws Exception {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");
        traineeDto.setAddress("Tashkent");
        traineeDto.setDateOfBirth(LocalDate.of(2000, 1, 1));

        String jsonRequest = objectMapper.writeValueAsString(traineeDto);

        mockMvc.perform(post("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    void testLoginTrainee_Success() throws Exception {
        UserDto userDto = new UserDto("john.doe", "securePass");

        // Optional: mock the service if you're using @WebMvcTest
        // when(traineeService.login("john.doe", "securePass")).thenReturn(true);

        mockMvc.perform(post("/trainee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully logged in"));
    }

    @Test
    void testLoginTrainee_Failed() throws Exception {
        UserDto userDto = new UserDto("wrong.user", "wrongPass");

        // Optional if mocking:
        // when(traineeService.login("wrong.user", "wrongPass")).thenReturn(false);

        mockMvc.perform(post("/trainee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Username or password incorrect"));
    }

    @Test
    void testChangeTraineePassword_InvalidCredentials() throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setUsername("trainee1");
        updateUserDto.setOldPassword("wrongOld");
        updateUserDto.setNewPassword("newPass");

        when(traineeService.updateTraineePassword(
                updateUserDto.getUsername(),
                updateUserDto.getOldPassword(),
                updateUserDto.getNewPassword()))
                .thenReturn(false);

        mockMvc.perform(put("/trainee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username or old password incorrect"));
    }

    @Test
    void testChangeTraineePassword_Success() throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setUsername("trainee1");
        updateUserDto.setOldPassword("oldPass");
        updateUserDto.setNewPassword("newSecurePass");

        // Mock service response
        when(traineeService.updateTraineePassword(
                updateUserDto.getUsername(),
                updateUserDto.getOldPassword(),
                updateUserDto.getNewPassword()))
                .thenReturn(true);

        mockMvc.perform(put("/trainee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully changed password"));
    }

    @Test
    void testGetTraineeProfile_Success() throws Exception {
        // Given a valid username
        String username = "trainee1";
        TraineeProfileDto traineeProfileDto = new TraineeProfileDto();
        traineeProfileDto.setUsername("trainee1");
        traineeProfileDto.setFirstName("John");
        traineeProfileDto.setLastName("Doe");

        // Mock service response
        when(traineeService.findTraineeByUsername(username))
                .thenReturn(traineeProfileDto);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/trainee/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainee1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void testGetTraineeProfile_NotFound() throws Exception {
        // Given a non-existent username
        String username = "nonexistentTrainee";

        // Mock service response for not found
        when(traineeService.findTraineeByUsername(username))
                .thenReturn(null);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/trainee/{username}", username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"));
    }

    @Test
    void testUpdateTrainee_Success() throws Exception {
        // Given a valid TraineeUpdateDto with updated information
        TraineeUpdateDto traineeDto = new TraineeUpdateDto();
        traineeDto.setUsername("trainee1");
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");
        traineeDto.setDateOfBirth(LocalDate.of(1990, 1, 1)); // Valid past date
        traineeDto.setAddress("123 Main St");
        traineeDto.setActive(true);

        TraineeProfileDto updatedTrainee = new TraineeProfileDto();
        updatedTrainee.setUsername("trainee1");
        updatedTrainee.setFirstName("John");
        updatedTrainee.setLastName("Doe");
        updatedTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedTrainee.setAddress("123 Main St");
        updatedTrainee.setActive(true);

        // Mock service response
        when(traineeService.updateTraineeProfile(traineeDto))
                .thenReturn(updatedTrainee);

        // Perform the PUT request and assert the response
        mockMvc.perform(put("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("trainee1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-01"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testUpdateTrainee_BadRequest_MissingFields() throws Exception {
        // Given an invalid TraineeUpdateDto with missing required fields
        TraineeUpdateDto traineeDto = new TraineeUpdateDto();
        traineeDto.setUsername("trainee1");
        // firstName is missing

        // Perform the PUT request and assert the response (400 Bad Request)
        mockMvc.perform(put("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("First name is required"));
    }

    @Test
    void testUpdateTrainee_BadRequest_InvalidDateOfBirth() throws Exception {
        // Given an invalid date of birth in the future
        TraineeUpdateDto traineeDto = new TraineeUpdateDto();
        traineeDto.setUsername("trainee1");
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");
        traineeDto.setDateOfBirth(LocalDate.of(2025, 1, 1)); // Invalid future date
        traineeDto.setActive(true);

        // Perform the PUT request and assert the response (400 Bad Request)
        mockMvc.perform(put("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Date of birth must be in the past"));
    }

    @Test
    void testUpdateTrainee_NotFound() throws Exception {
        // Given a non-existent username
        TraineeUpdateDto traineeDto = new TraineeUpdateDto();
        traineeDto.setUsername("nonexistentTrainee");
        traineeDto.setFirstName("Jane");
        traineeDto.setLastName("Doe");
        traineeDto.setDateOfBirth(LocalDate.of(1995, 1, 1));
        traineeDto.setActive(true);

        // Mock service response for not found
        when(traineeService.updateTraineeProfile(traineeDto))
                .thenThrow(new EntityNotFoundException("Trainee not found"));

        // Perform the PUT request and assert the response (404 Not Found)
        mockMvc.perform(put("/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Trainee not found"));
    }



}