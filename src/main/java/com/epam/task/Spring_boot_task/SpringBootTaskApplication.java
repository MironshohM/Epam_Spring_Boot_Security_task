package com.epam.task.Spring_boot_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SpringBootTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootTaskApplication.class, args);
	}

}
