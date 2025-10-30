package org.crm.student.notification_service.models;


import lombok.Data;

@Data
public class EmailNotificationRequest {
    private String to;
    private String subject;
    private String message;

}

