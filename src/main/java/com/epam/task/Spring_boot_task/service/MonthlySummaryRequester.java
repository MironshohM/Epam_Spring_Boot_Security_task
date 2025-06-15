package com.epam.task.Spring_boot_task.service;

import com.epam.task.Spring_boot_task.dtos.MonthlySummaryDTO;
import com.epam.task.Spring_boot_task.dtos.MonthlySummaryRequest;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonthlySummaryRequester {

    private final JmsTemplate jmsTemplate;

    public MonthlySummaryDTO requestMonthlySummary(String username, int year, int month) {
        MonthlySummaryRequest request = new MonthlySummaryRequest(username, year, month);

        Message response = jmsTemplate.sendAndReceive("monthly.summary.request.queue", session -> {
            ObjectMessage message = session.createObjectMessage(request);
            return message;
        });

        if (response instanceof ObjectMessage) {
            try {
                return (MonthlySummaryDTO) ((ObjectMessage) response).getObject();
            } catch (JMSException e) {
                throw new RuntimeException("Failed to extract MonthlySummaryDTO", e);
            }
        }
        throw new RuntimeException("Unexpected response type from summary service");
    }
}