package com.epam.task.Spring_boot_task.integration;



import com.epam.task.Spring_boot_task.dtos.MonthlySummaryDTO;
import com.epam.task.Spring_boot_task.dtos.MonthlySummaryRequest;
import com.epam.task.Spring_boot_task.dtos.TrainingSessionEventDTO;
import com.epam.task.Spring_boot_task.service.MonthlySummaryRequester;
import com.epam.task.Spring_boot_task.service.TrainingEventPublisher;
import io.cucumber.java.en.*;
import jakarta.jms.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;

@SpringBootTest
public class MainMicroserviceIntegrationStepDefinitions {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainingEventPublisher publisher;

    @Autowired
    private MonthlySummaryRequester summaryRequester;

    private final String TRAINING_QUEUE = "training-session-events";
    private final String SUMMARY_QUEUE = "monthly.summary.request.queue";

    private Message lastReceivedMessage;
    private MonthlySummaryDTO receivedSummary;

    @When("a training event with duration {int} is published for user {string}")
    public void publish_training_event(int duration, String username) {
        TrainingSessionEventDTO event = new TrainingSessionEventDTO();
        event.setUsername(username);
        event.setFirstName("John");
        event.setLastName("Doe");
        event.setActive(true);
        event.setTrainingDate(LocalDate.of(2025, 6, 1));
        event.setTrainingDuration(duration);
        event.setActionType("ADD");

        publisher.publishTrainingEvent(event);
        waitForProcessing();
    }

    @Then("the training-session-events queue should receive a message")
    public void verify_message_in_training_queue() {
        lastReceivedMessage = jmsTemplate.receive(TRAINING_QUEUE);
        Assertions.assertNotNull(lastReceivedMessage, "No message received on queue");
    }

    @Given("the summary response is mocked for user {string} with {int} minutes")
    public void mock_summary_response(String username, int duration) {
        jmsTemplate.setReceiveTimeout(2000);
        jmsTemplate.setDeliveryDelay(500);

        // Set up a listener on the summary request queue that replies
        new Thread(() -> {
            Message requestMsg = jmsTemplate.receive(SUMMARY_QUEUE);
            if (requestMsg instanceof ObjectMessage) {
                try {
                    ObjectMessage objMsg = (ObjectMessage) requestMsg;
                    MonthlySummaryRequest req = (MonthlySummaryRequest) objMsg.getObject();

                    MonthlySummaryDTO summary = new MonthlySummaryDTO(
                            req.getUsername(), "John", "Doe", true,
                            req.getYear(), req.getMonth(), duration
                    );

                    Destination replyTo = requestMsg.getJMSReplyTo();
                    jmsTemplate.convertAndSend(replyTo, summary);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @When("a monthly summary is requested for {string} in year {int} and month {int}")
    public void request_summary(String username, int year, int month) {
        receivedSummary = summaryRequester.requestMonthlySummary(username, year, month);
    }

    @Then("the returned summary should contain {int} minutes")
    public void verify_summary(int expected) {
        Assertions.assertNotNull(receivedSummary);
        Assertions.assertEquals(expected, receivedSummary.getTotalTrainingDuration());
    }

    private void waitForProcessing() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
    }
}
