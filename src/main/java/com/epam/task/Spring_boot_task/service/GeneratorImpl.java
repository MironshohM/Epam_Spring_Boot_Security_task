package com.epam.task.Spring_boot_task.service;

import org.hibernate.internal.build.AllowNonPortable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class GeneratorImpl implements Generator{
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public GeneratorImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public String generateUsername(String firstName, String lastName, int serialNumber) {
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        return serialNumber > 0 ? baseUsername + serialNumber : baseUsername;
    }

    public String generatePassword() {
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        System.out.println("Password: " + password.toString());
        String encodedPassword = passwordEncoder.encode(password.toString());
        return encodedPassword;
    }
}
