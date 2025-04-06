package com.epam.task.Spring_boot_task.controller;



import com.epam.task.Spring_boot_task.dtos.AddTrainingRequestDto;
import com.epam.task.Spring_boot_task.dtos.TrainingTypeDto;
import com.epam.task.Spring_boot_task.entity.Training;
import com.epam.task.Spring_boot_task.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Profile("dev")
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Validated
public class TrainingController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingController.class);
    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    // Ex: 14 - Add Training
    @Operation(
            summary = "Add a new Training",
            description = "Creates a new training session between a trainee and a trainer.",
            tags = { "Training", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Failed to add training",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/training")
    public ResponseEntity<String> addTraining(@RequestBody @Valid AddTrainingRequestDto request) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);

        try {
            logger.info("TransactionID: {} - Received request to add training: {}", transactionId, request);

            boolean success = trainingService.addTraining(
                    request.getTraineeUsername(), request.getTrainerUsername(),
                    new Training(request.getTrainingName(), request.getTrainingDate(), request.getTrainingDuration())
            );

            if (success) {
                logger.info("TransactionID: {} - Training added successfully", transactionId);
                return ResponseEntity.ok("Training added successfully");
            } else {
                logger.warn("TransactionID: {} - Failed to add training", transactionId);
                return ResponseEntity.badRequest().body("Failed to add Training");
            }
        } finally {
            MDC.clear(); // Ensures MDC is always cleared
        }
    }


    // Ex: 17 - Get Training Types
    @Operation(
            summary = "Get all training types",
            description = "Retrieves a list of available training types.",
            tags = { "Training", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved training types",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTypeDto[].class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/training/types")
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);

        try {
            logger.info("TransactionID: {} - Fetching all training types", transactionId);

            List<TrainingTypeDto> trainingTypes = trainingService.getAllTrainingTypes();

            logger.info("TransactionID: {} - Successfully fetched {} training types", transactionId, trainingTypes.size());
            return ResponseEntity.ok(trainingTypes);
        } finally {
            MDC.clear(); // Ensures MDC is always cleared
        }
    }
}