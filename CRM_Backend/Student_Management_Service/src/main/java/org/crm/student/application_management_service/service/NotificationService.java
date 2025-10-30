package org.crm.student.application_management_service.service;

import org.crm.student.application_management_service.email.EmailNotificationRequest;
import org.crm.student.application_management_service.email.SmsNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    private static final String EMAIL_NOTIFICATION_URL = "http://localhost:8085/api/notifications/email";
    private static final String SMS_NOTIFICATION_URL = "http://localhost:8085/api/notifications/sms";

    @Autowired
    private RestTemplate restTemplate;

    public void sendEmailNotification(String email, String subject, String candidateName) {
        EmailNotificationRequest emailRequest = new EmailNotificationRequest();
        emailRequest.setTo(email);
        emailRequest.setSubject(subject);

        // Call the Notification service's email API
        restTemplate.postForObject(EMAIL_NOTIFICATION_URL + "?candidateName=" + candidateName, emailRequest, String.class);
    }

    public void sendSmsNotification(String phoneNumber, String message) {
        SmsNotificationRequest smsRequest = new SmsNotificationRequest();
        smsRequest.setPhoneNumber(phoneNumber);
        smsRequest.setMessage(message);

        // Call the Notification service's SMS API
        restTemplate.postForObject(SMS_NOTIFICATION_URL, smsRequest, String.class);
    }
}

