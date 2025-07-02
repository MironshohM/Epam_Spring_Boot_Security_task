Feature: Integration test of main microservice with ActiveMQ

  Scenario: Publish training session event to ActiveMQ
    When a training event with duration 60 is published for user "john123"
    Then the training-session-events queue should receive a message

  Scenario: Request monthly summary and receive response
    Given the summary response is mocked for user "john123" with 90 minutes
    When a monthly summary is requested for "john123" in year 2025 and month 6
    Then the returned summary should contain 90 minutes
