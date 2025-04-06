package com.epam.task.Spring_boot_task.controller;



import com.epam.task.Spring_boot_task.dtos.*;
import com.epam.task.Spring_boot_task.entity.Trainee;
import com.epam.task.Spring_boot_task.exceptions.InvalidLoginException;
import com.epam.task.Spring_boot_task.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("dev")
@RequestMapping("/api")
@Validated
@Tag(name = "Trainee Controller", description = "Endpoints related to trainees")
public class TraineeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeController.class);

    private final TraineeService traineeService;

    @Autowired
    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    // Ex:1 - Register Trainee
    @Operation(summary = "Create a new Trainee", tags = { "Trainee", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {
                    @Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
    @PostMapping("/trainee")
    public ResponseEntity<UserDto> registerTrainee(@Valid @RequestBody TraineeDto traineeDto) {
        LOGGER.info("Registering new trainee: {}", (traineeDto.getLastName())+" "+traineeDto.getFirstName());
        Trainee savedTrainee = traineeService.saveTrainee(traineeDto);
        UserDto userDto = new UserDto(savedTrainee.getUsername(), savedTrainee.getPassword());
        LOGGER.info("Trainee registered successfully: {}", savedTrainee.getUsername());
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    // Ex:3 - Login Trainee
    @Operation(summary = "Login to system", tags = { "trainee/login", "post" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/trainee/login") // Fixed incorrect GET method
    public ResponseEntity<String> loginTrainee(@Valid @RequestBody UserDto userDto) {
        LOGGER.info("Trainee login attempt: {}", userDto.getUsername());
        boolean isAuthenticated = traineeService.login(userDto.getUsername(), userDto.getPassword());
        if (isAuthenticated) {
            LOGGER.info("Trainee logged in successfully: {}", userDto.getUsername());
            return ResponseEntity.ok("Successfully logged in");
        } else {
            LOGGER.warn("Invalid login attempt for trainee: {}", userDto.getUsername());
            throw new InvalidLoginException("Username or password incorrect");
        }
    }

    // Ex:4 - Change
    @Operation(summary = "Changing trainee's password", tags = { "trainee/login", "put" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully changed password",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainee/login")
    public ResponseEntity<String> changeTraineePassword(@Valid @RequestBody UpdateUserDto userDto) {
        LOGGER.info("Password change request for trainee: {}", userDto.getUsername());
        boolean updated = traineeService.updateTraineePassword(userDto.getUsername(), userDto.getOldPassword(), userDto.getNewPassword());
        if (updated) {
            LOGGER.info("Password changed successfully for trainee: {}", userDto.getUsername());
            return ResponseEntity.ok("Successfully changed password");
        } else {
            LOGGER.warn("Failed password change attempt for trainee: {}", userDto.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or old password incorrect");
        }
    }

    // Ex:5 - Get Trainee Profile
    @Operation(summary = "Get Trainee by Username", tags = { "Trainee", "get" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainee profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TraineeProfileDto.class))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/trainee/{username}")
    public ResponseEntity<TraineeProfileDto> getTraineeProfile(@PathVariable String username) {
        LOGGER.info("Fetching profile for trainee: {}", username);
        TraineeProfileDto traineeProfileDto = traineeService.findTraineeByUsername(username);
        LOGGER.info("Successfully retrieved profile for trainee: {}", username);
        return ResponseEntity.ok(traineeProfileDto);
    }


    // Ex:6 - Update Trainee Profile
    @Operation(summary = "Update Trainee Profile", tags = { "Trainee", "put" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TraineeProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainee")
    public ResponseEntity<TraineeProfileDto> updateTrainee(@Valid @RequestBody TraineeUpdateDto traineeDto) {
        LOGGER.info("Updating trainee profile: {}", traineeDto.getUsername());
        TraineeProfileDto updatedTrainee = traineeService.updateTraineeProfile(traineeDto);
        LOGGER.info("Trainee profile updated successfully: {}", traineeDto.getUsername());
        return ResponseEntity.ok(updatedTrainee);
    }


    // Ex:7 - Delete Trainee
    @Operation(summary = "Delete a Trainee by Username", tags = { "Trainee", "delete" })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/trainee/{username}")
    public ResponseEntity<String> deleteTrainee(@PathVariable String username) {
        LOGGER.info("Deleting trainee: {}", username);
        traineeService.deleteTraineeByUsername(username);
        LOGGER.info("Trainee deleted successfully: {}", username);
        return ResponseEntity.ok("Trainee with username: " + username + " deleted");
    }


    // Ex:11 - Update Trainee's Trainer List
    @Operation(
            summary = "Update a Trainee's Trainer List",
            description = "Updates the list of trainers assigned to a specific trainee.",
            tags = { "Trainee", "put" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated trainer list",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainerDto.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/trainee/trainers/{username}")
    public ResponseEntity<List<TrainerDto>> updateTraineeTrainerList(
            @PathVariable String username,
            @RequestBody @Valid TrainerListDto trainerListDto) {
        LOGGER.info("Updating trainer list for trainee: {}", username);
        List<TrainerDto> updatedTrainers = traineeService.updateTraineeTrainerList(username, trainerListDto.getUsernames());
        LOGGER.info("Trainer list updated for trainee: {}", username);
        return ResponseEntity.ok(updatedTrainers);
    }


    // Ex:12 - Get Trainee's Trainings (Fixed incorrect method type)
    @Operation(
            summary = "Retrieve Trainings for a Trainee",
            description = "Fetches all training sessions associated with a given trainee.",
            tags = { "Trainee", "trainings", "post" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trainings",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrainingDto.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/trainee/trainings")
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(@RequestBody @Valid TraineeTrainingRequestDto request) {
        LOGGER.info("Fetching trainings for trainee: {}", request.getUsername());
        List<TrainingDto> trainings = traineeService.getTraineeTrainings(request);
        LOGGER.info("Retrieved {} trainings for trainee: {}", trainings.size(), request.getUsername());
        return ResponseEntity.ok(trainings);
    }


    // Ex:15 - Activate/Deactivate Trainee
    @Operation(
            summary = "Activate or Deactivate a Trainee",
            description = "Updates the activation status of a trainee. Pass `true` to activate and `false` to deactivate.",
            tags = { "Trainee", "patch" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee activation status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Trainee not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/trainee/activate")
    public ResponseEntity<String> updateTraineeActivation(@RequestBody @Valid TraineeActivateDto request) {
        LOGGER.info("{} trainee: {}", request.isActive() ? "Activating" : "Deactivating", request.getUsername());
        traineeService.updateTraineeStatus(request.getUsername(), request.isActive());
        String message = request.isActive() ? "Trainee activated successfully" : "Trainee deactivated successfully";
        LOGGER.info("Trainee {}: {}", request.isActive() ? "activated" : "deactivated", request.getUsername());
        return ResponseEntity.ok(message);
    }

}
