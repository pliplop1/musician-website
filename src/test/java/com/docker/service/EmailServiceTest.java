package com.docker.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest(properties = {
        "email.simulation.mode=true",
        "spring.mail.username=test@example.com"
})
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendSimpleEmail_inSimulationMode_doesNotThrow() {
        assertDoesNotThrow(() ->
                emailService.sendSimpleEmail("user@example.com", "Sujet", "Corps de message"));
    }

    @Test
    void sendPasswordResetEmail_inSimulationMode_doesNotThrow() {
        assertDoesNotThrow(() ->
                emailService.sendPasswordResetEmail("user@example.com", "John", "https://example.com/reset?token=abc"));
    }
}

