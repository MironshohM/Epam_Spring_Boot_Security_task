package com.epam.task.Spring_boot_task.controller;



import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainer;
import com.epam.task.Spring_boot_task.exceptions.InvalidLoginException;
import com.epam.task.Spring_boot_task.service.TrainerService;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Profile("dev")
@RequestMapping("/api")
public class TrainerController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerController.class);
    private final TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    private void startTransactionLogging() {
        String transactionId = UUID.randomUUID().toString(); // Generate transactionId
        MDC.put("transactionId", transactionId);
    }

    private void endTransactionLogging() {
        MDC.clear(); // Clear MDC after request is processed
    }

    //Ex:2 - Register Trainer
    @Operation(
            summary = "Register a new Trainer",
            description = "Creates a new trainer account and returns the generated username and password.",
            tags = { "Trainer", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/trainer")
    public ResponseEntity<UserDto> registerTrainer(@Valid @RequestBody TrainerDto trainerDto) {
        startTransactionLogging();
        logger.info("Received request to register trainer: {}", trainerDto.getUsername());

        Trainer savedTrainer = trainerService.saveTrainer(trainerDto);
        UserDto userDto = new UserDto(savedTrainer.getUsername(), savedTrainer.getPassword());

        logger.info("Trainer registered successfully: {}", savedTrainer.getUsername());
        endTransactionLogging();
        return ResponseEntity.status(201).body(userDto);
    }

    //Ex:3 - Trainer Login
    @Operation(
            summary = "Login as a Trainer",
            description = "Validates trainer credentials and returns a success message if authentication is successful.",
            tags = { "Trainer", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/trainer/login")
    public ResponseEntity<String> loginTrainer(@Valid @RequestBody UserDto userDto) {
        startTransactionLogging();
        logger.info("Login attempt for trainer: {}", userDto.getUsername());

        boolean isAuthenticated = trainerService.login(userDto.getUsername(), userDto.getPassword());
        if (isAuthenticated) {
            logger.info("Trainer {} successfully logged in", userDto.getUsername());
            endTransactionLogging();
            return ResponseEntity.ok("Successfully logged in");
        } else {
            logger.warn("Login failed for trainer: {}", userDto.getUsername());
            throw new InvalidLoginException("Username or password incorrect");
        }
    }

    //Ex:4 - Change Trainer Login
    @Operation(
            summary = "Change Trainer Password",
            description = "Updates the trainer's password after verifying the old password.",
            tags = { "Trainer", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid old password or username",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainer/login")
    public ResponseEntity<String> changeTrainerLogin(@Valid @RequestBody UpdateUserDto userDto) {
        startTransactionLogging();
        logger.info("Request to change password for trainer: {}", userDto.getUsername());

        boolean isUpdated = trainerService.updatePassword(userDto.getUsername(), userDto.getOldPassword(), userDto.getNewPassword());
        if (isUpdated) {
            logger.info("Password changed successfully for trainer: {}", userDto.getUsername());
            endTransactionLogging();
            return ResponseEntity.ok("Successfully changed password");
        } else {
            logger.warn("Password change failed for trainer: {}", userDto.getUsername());
            endTransactionLogging();
            return ResponseEntity.badRequest().body("Username or old password incorrect");
        }
    }

    //Ex:8 - Get Trainer Profile
    @Operation(
            summary = "Get Trainer Profile",
            description = "Fetches the profile information of a trainer by username.",
            tags = { "Trainer", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully fetched trainer profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/trainer/{username}")
    public ResponseEntity<TrainerProfileDto> getTrainerProfile(@PathVariable String username) {
        startTransactionLogging();
        logger.info("Fetching profile for trainer: {}", username);

        TrainerProfileDto profileDto = trainerService.findTrainerProfileByUsername(username);
        logger.info("Successfully fetched profile for trainer: {}", username);
        endTransactionLogging();
        return ResponseEntity.ok(profileDto);
    }

    //Ex:9 - Update Trainer
    @Operation(
            summary = "Update Trainer Profile",
            description = "Updates the profile information of a trainer.",
            tags = { "Trainer", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainer")
    public ResponseEntity<TrainerProfileDto> updateTrainer(@RequestBody @Valid TrainerUpdateDto updateDto) {
        startTransactionLogging();
        logger.info("Updating profile for trainer: {}", updateDto.getUsername());

        TrainerProfileDto updatedTrainer = trainerService.updateTrainerProfile(updateDto);
        logger.info("Trainer {} updated successfully", updateDto.getUsername());

        endTransactionLogging();
        return ResponseEntity.ok(updatedTrainer);
    }

    //Ex:10 - Get Unassigned Trainers
    @Operation(
            summary = "Get Unassigned Trainers",
            description = "Retrieves a list of trainers who are not assigned to a specific trainee.",
            tags = { "Trainer", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of unassigned trainers retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerDto.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/trainer/unassigned/{username}")
    public ResponseEntity<List<TrainerDto>> getUnassignedTrainers(@PathVariable String username) {
        startTransactionLogging();
        logger.info("Fetching unassigned trainers for trainee: {}", username);

        List<TrainerDto> trainers = trainerService.getUnassignedTrainers(username);
        if (trainers.isEmpty()) {
            logger.info("No unassigned trainers found for trainee: {}", username);
            endTransactionLogging();
            return ResponseEntity.noContent().build();
        }

        logger.info("Successfully fetched unassigned trainers for trainee: {}", username);
        endTransactionLogging();
        return ResponseEntity.ok(trainers);
    }

    //Ex:13 - Get Trainer Trainings
    @Operation(
            summary = "Get Trainings for Trainer",
            description = "Fetches all trainings assigned to a trainer based on their username.",
            tags = { "Trainer", "get" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainings",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerTrainingResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/trainer/trainings")
    public ResponseEntity<List<TrainerTrainingResponseDto>> getTrainerTrainings(@Valid TrainerTrainingRequestDto request) {
        startTransactionLogging();
        logger.info("Fetching trainings for trainer: {}", request.getUsername());

        List<TrainerTrainingResponseDto> trainings = trainerService.getTrainerTrainings(request);
        logger.info("Successfully fetched trainings for trainer: {}", request.getUsername());

        endTransactionLogging();
        return ResponseEntity.ok(trainings);
    }

    //Ex:16 - Update Trainer Activation
    @Operation(
            summary = "Activate/Deactivate Trainer",
            description = "Updates the activation status of a trainer based on the given username.",
            tags = { "Trainer", "patch" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated activation status",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainer not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/trainer/activate")
    public ResponseEntity<String> updateTrainerActivation(@RequestBody @Valid TrainerActivateDto request) {
        startTransactionLogging();
        logger.info("Updating activation status for trainer: {}", request.getUsername());

        trainerService.updateTrainerStatus(request.getUsername(), request.isActive());
        String message = request.isActive() ? "Trainer activated successfully" : "Trainer deactivated successfully";

        logger.info(message + ": {}", request.getUsername());
        endTransactionLogging();
        return ResponseEntity.ok(message);
    }
}