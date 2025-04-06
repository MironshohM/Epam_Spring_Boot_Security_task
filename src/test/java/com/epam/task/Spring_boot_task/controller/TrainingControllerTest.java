package com.epam.task.Spring_boot_task.controller;


import com.epam.task.Spring_boot_task.dtos.AddTrainingRequestDto;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.service.TrainingService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TrainingController.class)
public class TrainingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddTraining_Success() throws Exception {
        // Given a valid training request
        AddTrainingRequestDto request = new AddTrainingRequestDto("trainee1", "trainer1", "Java Basics",
                LocalDate.now(), 120); // Example 2-hour training

        // Mock the service response to return true (training added successfully)
        when(trainingService.addTraining(eq("trainee1"), eq("trainer1"), any(Training.class)))
                .thenReturn(true);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk()) // Assert HTTP 200 status code
                .andExpect(content().string("Training added successfully"));
    }

    @Test
    void testAddTraining_Fail() throws Exception {
        // Given a valid training request
        AddTrainingRequestDto request = new AddTrainingRequestDto("trainee1", "trainer1", "Java Basics",
                LocalDate.now(), 120); // Example 2-hour training

        // Mock the service response to return false (training failed to add)
        when(trainingService.addTraining(eq("trainee1"), eq("trainer1"), any(Training.class)))
                .thenReturn(false);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(content().string("Failed to add Training"));
    }

    @Test
    void testAddTraining_BadRequest_ValidationError() throws Exception {
        // Given an invalid training request (e.g., missing required fields)
        AddTrainingRequestDto invalidRequest = new AddTrainingRequestDto("", "", "",
                null, null); // Invalid fields

        // Perform the POST request and assert the response
        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testAddTraining_BadRequest_EmptyTrainingName() throws Exception {
        // Given a training request with an empty training name
        AddTrainingRequestDto request = new AddTrainingRequestDto("trainee1", "trainer1", "",
                LocalDate.now(), 120);

        // Perform the POST request and assert the response
        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(jsonPath("$.message").value("Training name is required"));
    }

    @Test
    void testAddTraining_BadRequest_InvalidTrainingDuration() throws Exception {
        // Given a training request with invalid duration (negative value)
        AddTrainingRequestDto request = new AddTrainingRequestDto("trainee1", "trainer1", "Java Basics",
                LocalDate.now(), -1); // Invalid duration

        // Perform the POST request and assert the response
        mockMvc.perform(post("/training")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest()) // Assert HTTP 400 status code
                .andExpect(jsonPath("$.message").value("Training duration must be positive"));
    }


    private String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }



}
