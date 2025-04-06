package com.epam.task.Spring_boot_task.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Trainer API",
                version = "1.0",
                description = "Trainer authentication and profile management",
                contact = @Contact(name = "Your Name", email = "your.email@example.com")
        )
)
public class OpenAPIConfig {
}