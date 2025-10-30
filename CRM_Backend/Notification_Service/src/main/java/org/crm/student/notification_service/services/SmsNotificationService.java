package org.crm.student.notification_service.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.crm.student.notification_service.models.SmsNotificationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    @Value("${spring.twilio.accountSid}")
    private String accountSid;

    @Value("${spring.twilio.authToken}")
    private String authToken;

    @Value("${spring.twilio.phoneNumber}")
    private String fromPhoneNumber;

    // Initialize Twilio after properties have been injected
    @PostConstruct
    public void init() {
        try {
            Twilio.init(accountSid, authToken);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Twilio with the provided credentials", e);
        }
    }

    public void sendSms(SmsNotificationRequest request) {
        try {
            // Ensure that the phone number is valid before attempting to send an SMS
            if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required.");
            }

            if (request.getMessage() == null || request.getMessage().isEmpty()) {
                throw new IllegalArgumentException("Message content is required.");
            }

            // Send SMS through Twilio API
            Message.creator(
                            new PhoneNumber(request.getPhoneNumber()),  // To phone number
                            new PhoneNumber(fromPhoneNumber),           // From phone number
                            request.getMessage())                       // SMS message
                    .create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS to " + request.getPhoneNumber(), e);
        }
    }
}
