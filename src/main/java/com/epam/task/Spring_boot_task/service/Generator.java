package com.epam.task.Spring_boot_task.service;

public interface Generator {
    String generateUsername(String firstName, String lastName, int serialNumber);

    String generatePassword();

}
