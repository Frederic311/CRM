package org.crm.student.notification_service.models;

import lombok.Data;

@Data
public class SmsNotificationRequest {
    private String phoneNumber;
    private String message;

}
