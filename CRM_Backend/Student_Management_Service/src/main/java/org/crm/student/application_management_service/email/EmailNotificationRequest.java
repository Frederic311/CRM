package org.crm.student.application_management_service.email;

import lombok.Data;

@Data
public class EmailNotificationRequest {
    private String to;
    private String subject;
    private String message;

}
