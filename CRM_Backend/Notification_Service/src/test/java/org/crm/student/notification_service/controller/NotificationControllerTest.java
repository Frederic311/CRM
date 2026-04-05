package org.crm.student.notification_service.controller;

import org.crm.student.notification_service.models.EmailNotificationRequest;
import org.crm.student.notification_service.models.SmsNotificationRequest;
import org.crm.student.notification_service.services.EmailNotificationService;
import org.crm.student.notification_service.services.SmsNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private EmailNotificationService emailService;

    @Mock
    private SmsNotificationService smsService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void sendTaskAssignmentEmail_shouldReturnBadRequestWhenFieldsAreMissing() {
        EmailNotificationRequest request = new EmailNotificationRequest();
        request.setTo("user@example.com");
        request.setSubject(null);
        request.setMessage("message");

        ResponseEntity<String> response = notificationController.sendTaskAssignmentEmail(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: All fields (to, subject, and message) are required.", response.getBody());
    }

    @Test
    void sendSms_shouldReturnOkWhenServiceSucceeds() {
        SmsNotificationRequest request = new SmsNotificationRequest();
        request.setPhoneNumber("+123456789");
        request.setMessage("Hello");

        doNothing().when(smsService).sendSms(any(SmsNotificationRequest.class));

        ResponseEntity<String> response = notificationController.sendSms(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SMS sent successfully!", response.getBody());
        verify(smsService).sendSms(request);
    }
}
