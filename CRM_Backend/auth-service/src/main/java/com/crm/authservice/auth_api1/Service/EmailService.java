package com.crm.authservice.auth_api1.Service;

import com.crm.authservice.auth_api1.models.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject,
            String temporaryPassword  // Added temporaryPassword here
    ) throws MessagingException {
        // getName(), not name(): name() returns the constant name, while the
        // field is what matches the Thymeleaf file name. Thymeleaf resolves
        // classpath:/templates/<name>.html, and a mismatch raises a
        // TemplateInputException from inside this @Async void method, where
        // nobody observes it. The mail is then silently never sent.
        //
        // The fallback is a real template: "confirm-email" did not exist, so a
        // null enum failed the same way.
        String templateName = (emailTemplate != null)
                ? emailTemplate.getName()
                : EmailTemplateName.ACTIVATE_ACCOUNT.getName();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);
        properties.put("temporaryPassword", temporaryPassword);  // Add temporary password to properties

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("kamdem.guy@institutsaintjean.org");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        helper.setText(template, true);

        mailSender.send(mimeMessage);


    }
}
