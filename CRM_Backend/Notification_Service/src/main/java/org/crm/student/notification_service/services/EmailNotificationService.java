package org.crm.student.notification_service.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.crm.student.notification_service.models.EmailNotificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Map;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmail(EmailNotificationRequest request, Map<String, Object> templateVariables) throws MessagingException, IOException {
        // Validate request fields
        if (request.getTo() == null || request.getTo().isEmpty()) {
            throw new IllegalArgumentException("Recipient email address is required.");
        }
        if (request.getSubject() == null || request.getSubject().isEmpty()) {
            throw new IllegalArgumentException("Email subject is required.");
        }

        // Prepare the Thymeleaf context with the variables for the template
        Context context = new Context();
        context.setVariables(templateVariables);

        // Generate the HTML content using the template
        String htmlContent = templateEngine.process("candidate-notification", context);

        // Create and set up a MIME message with HTML content
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
        helper.setTo(request.getTo());
        helper.setSubject(request.getSubject());
        helper.setText(htmlContent, true); // true enables HTML

        // Attach a default file from resources
        ClassPathResource resource = new ClassPathResource("/static/candidate_email.pdf");
        helper.addAttachment("admission.pdf", resource);

        // Send the email
        mailSender.send(message);
    }
    public void sendTaskAssignmentEmail(String recipientEmail, String taskName, String taskDescription) throws MessagingException {
        // Prepare the Thymeleaf context with the variables for the template
        Context context = new Context();
        context.setVariable("name", taskName);  // You can dynamically get the user's name if needed
        context.setVariable("taskName", taskName);
        context.setVariable("taskDescription", taskDescription);

        // Generate the HTML content using the template
        String htmlContent = templateEngine.process("task-assignment", context);

        // Create and set up the MIME message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
        helper.setTo(recipientEmail);
        helper.setSubject("New Task Assigned: " + taskName);
        helper.setText(htmlContent, true);  // true enables HTML content

        // Send the email
        mailSender.send(message);
    }
    public void sendCompleteTaskAssignmentEmail(String recipientEmail, String taskName, String taskDescription) throws MessagingException {
        // Prepare the Thymeleaf context with the variables for the template
        Context context = new Context();
        context.setVariable("name", taskName);  // You can dynamically get the user's name if needed
        context.setVariable("taskName", taskName);
        context.setVariable("taskDescription", taskDescription);

        // Generate the HTML content using the template
        String htmlContent = templateEngine.process("task-completion", context);

        // Create and set up the MIME message
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
        helper.setTo(recipientEmail);
        helper.setSubject("New Task Assigned: " + taskName);
        helper.setText(htmlContent, true);  // true enables HTML content

        // Send the email
        mailSender.send(message);
    }
}


