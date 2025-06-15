package com.epam.task.Spring_boot_task.service;

import com.epam.task.Spring_boot_task.dtos.TrainingSessionEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingEventPublisher {

    private final JmsTemplate jmsTemplate;

    public void publishTrainingEvent(TrainingSessionEventDTO event) {
        jmsTemplate.convertAndSend("training-session-events", event);
    }
}
