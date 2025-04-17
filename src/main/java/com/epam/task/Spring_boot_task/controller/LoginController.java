package com.epam.task.Spring_boot_task.controller;

import com.epam.task.Spring_boot_task.dtos.LoginResponse;
import com.epam.task.Spring_boot_task.dtos.UserDto;
import com.epam.task.Spring_boot_task.service.LoginAttemptService;
import com.epam.task.Spring_boot_task.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           LoginAttemptService loginAttemptService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDto userDto) {
        String username = userDto.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("User is temporarily blocked due to multiple failed login attempts. Try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, userDto.getPassword())
            );

            loginAttemptService.loginSucceeded(username);

            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
