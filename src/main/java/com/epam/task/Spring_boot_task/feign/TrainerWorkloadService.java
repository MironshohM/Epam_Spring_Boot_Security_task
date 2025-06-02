package com.epam.task.Spring_boot_task.feign;

import com.epam.task.Spring_boot_task.dtos.MonthlySummaryDTO;
import com.epam.task.Spring_boot_task.dtos.TrainingSessionEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="TRAININGSUMMARY")
public interface TrainerWorkloadService {
    @PostMapping("/update")
    ResponseEntity<String> updateWorkload(@RequestBody TrainingSessionEventDTO dto);

    // Retrieve summary for a specific trainer in a specific month
    @GetMapping("/{username}/{year}/{month}")
    ResponseEntity<MonthlySummaryDTO> getMonthlySummary(
            @PathVariable String username,
            @PathVariable int year,
            @PathVariable int month);
}
