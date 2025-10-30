package org.crm.student.task_management_service.model;

import lombok.Data;

@Data
public class EmailNotificationRequest {
    private String to;
    private String subject;
    private String message;

}
