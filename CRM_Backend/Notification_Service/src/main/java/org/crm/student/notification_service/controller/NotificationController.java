package org.crm.student.notification_service.controller;

import jakarta.mail.MessagingException;
import org.crm.student.notification_service.models.EmailNotificationRequest;
import org.crm.student.notification_service.models.SmsNotificationRequest;
import org.crm.student.notification_service.models.WhatsAppNotificationRequest;
import org.crm.student.notification_service.services.EmailNotificationService;
import org.crm.student.notification_service.services.SmsNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailNotificationService emailService;

   @Autowired
    private SmsNotificationService smsService;
    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailNotificationRequest request) {
        try {
            // Dummy data for template variables (can be updated dynamically)
            Map<String, Object> templateVariables = Map.of("name", "Candidate Name");

            emailService.sendEmail(request, templateVariables);
            return ResponseEntity.ok("Email with attachment sent successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (IOException | MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/sms")
    public ResponseEntity<String> sendSms(@RequestBody SmsNotificationRequest request) {
        try {
            smsService.sendSms(request);
            return ResponseEntity.ok("SMS sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending SMS: " + e.getMessage());
        }
    }
    @PostMapping("/task-assignment")
    public ResponseEntity<String> sendTaskAssignmentEmail(@RequestBody EmailNotificationRequest request) {
        try {
            // Ensure the request contains necessary fields
            if (request.getTo() == null || request.getSubject() == null || request.getMessage() == null) {
                throw new IllegalArgumentException("All fields (to, subject, and message) are required.");
            }

            // Call the email service to send the email
            emailService.sendTaskAssignmentEmail(request.getTo(), request.getSubject(), request.getMessage());
            return ResponseEntity.ok("Task assignment email sent successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send task assignment email: " + e.getMessage());
        }
    }
    @PostMapping("/task-completion")
    public ResponseEntity<String> sendCompleteTaskAssignmentEmail(@RequestBody EmailNotificationRequest request) {
        try {
            // Ensure the request contains necessary fields
            if (request.getTo() == null || request.getSubject() == null || request.getMessage() == null) {
                throw new IllegalArgumentException("All fields (to, subject, and message) are required.");
            }

            // Call the email service to send the email
            emailService.sendCompleteTaskAssignmentEmail(request.getTo(), request.getSubject(), request.getMessage());
            return ResponseEntity.ok("Task completion email sent successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send task completion email: " + e.getMessage());
        }
    }



}
