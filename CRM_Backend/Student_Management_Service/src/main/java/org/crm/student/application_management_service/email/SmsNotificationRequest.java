package org.crm.student.application_management_service.email;

import lombok.Data;

@Data

public class SmsNotificationRequest {
    private String phoneNumber;  // Recipient's phone number
    private String message;  }
