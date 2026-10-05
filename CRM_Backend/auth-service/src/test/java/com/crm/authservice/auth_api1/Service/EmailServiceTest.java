package com.crm.authservice.auth_api1.Service;

import com.crm.authservice.auth_api1.models.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * EmailService has to ask Thymeleaf for the template an EmailTemplateName
 * carries in its field, and a template file has to exist under that exact name.
 *
 * It used emailTemplate.name(), which returns the constant name, while the
 * files are named after the field: ACTIVATE_ACCOUNT resolved to
 * ACTIVATE_ACCOUNT.html while the file is activate_account.html.
 *
 * The two were not interchangeable either. Switching the call to getName()
 * alone would have traded one missing template for another, because
 * RESET_PASSWORD carried the field "reset_password" against a file named
 * RESET_PASSWORD.html. The file names and the enum fields now both follow
 * lower_snake_case.
 *
 * Two things had to be right for this to be testable at all.
 *
 * The failure was silent in production: sendEmail is @Async and returns void,
 * so the TemplateInputException it raised had no caller to observe it, and the
 * message was simply never sent while signup returned 200.
 *
 * And it only ever appears on a case-sensitive filesystem. On Windows,
 * ClassLoader.getResource matches activate_account.html for a request of
 * ACTIVATE_ACCOUNT.html, so simply rendering the templates passes happily
 * against the broken code. Both halves here are therefore immune to that: the
 * name EmailService asks for is recorded before any lookup happens, and the
 * spelling is compared against the files that exist rather than resolved by
 * name. The deployment is Linux, so the comparison has to be exact everywhere.
 */
class EmailServiceTest {

    private static final String CONFIRMATION_URL = "http://localhost:4200/activate-account";

    private final RecordingTemplateResolver resolver = new RecordingTemplateResolver();
    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
    private final SpringTemplateEngine engine = engineFor(resolver);
    private final EmailService emailService = new EmailService(mailSender, engine);

    @BeforeEach
    void stubOutgoingMessages() {
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);
    }

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    @DisplayName("a template file exists with exactly the name the enum carries")
    void everyTemplateNamedByTheEnumExistsWithThatSpelling(EmailTemplateName template) throws IOException {
        assertThat(templateFileNames()).contains(template.getName() + ".html");
    }

    @Test
    @DisplayName("no template file relies on a case difference to be found")
    void noTemplateFileReliesOnACaseDifference() throws IOException {
        assertThat(templateFileNames())
                .allMatch(name -> name.equals(name.toLowerCase(Locale.ROOT)),
                        "templates are resolved on Linux, where file names are case-sensitive");
    }

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    @DisplayName("sendEmail asks Thymeleaf for the name the enum field carries")
    void sendEmail_resolvesTheTemplateThroughTheEnumField(EmailTemplateName template) throws MessagingException {
        emailService.sendEmail("to@example.com", "ada", template, CONFIRMATION_URL, "123456", "subject", "secret");

        assertThat(resolver.requested).containsExactly(template.getName());
    }

    @ParameterizedTest
    @EnumSource(EmailTemplateName.class)
    @DisplayName("every template named by the enum renders and is sent")
    void everyTemplateNamedByTheEnumRendersAndIsSent(EmailTemplateName template) throws MessagingException {
        emailService.sendEmail("to@example.com", "ada", template, CONFIRMATION_URL, "123456", "subject", "secret");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendEmail falls back to a template that exists when the enum is null")
    void sendEmail_fallsBackToAnExistingTemplate() throws MessagingException, IOException {
        emailService.sendEmail("to@example.com", "ada", null, CONFIRMATION_URL, "123456", "subject", "secret");

        assertThat(resolver.requested).hasSize(1);
        assertThat(templateFileNames()).contains(resolver.requested.get(0) + ".html");
    }

    @ParameterizedTest
    @EnumSource(value = EmailTemplateName.class, names = {"ACTIVATE_ACCOUNT", "RESET_PASSWORD"})
    @DisplayName("the url sent with the email is rendered, so the mail is actionable")
    void theUrlSentWithTheEmailIsRendered(EmailTemplateName template) throws Exception {
        emailService.sendEmail("to@example.com", "ada", template, CONFIRMATION_URL, "123456", "subject", "secret");

        assertThat(allText(mimeMessage)).contains(CONFIRMATION_URL);
    }

    @Test
    @DisplayName("the confirmation mail, which is sent without a url, renders no link")
    void theConfirmationMailRendersNoLink() throws Exception {
        emailService.sendEmail("to@example.com", "ada", EmailTemplateName.PASSWORD_RESET_CONFIRMATION,
                null, null, "subject", "secret");

        assertThat(resolver.requested).containsExactly(EmailTemplateName.PASSWORD_RESET_CONFIRMATION.getName());
        assertThat(allText(mimeMessage)).doesNotContain("null");
    }

    @Test
    @DisplayName("the context reaches the rendered body")
    void theContextReachesTheRenderedBody() throws Exception {
        emailService.sendEmail("to@example.com", "ada", EmailTemplateName.ACTIVATE_ACCOUNT,
                CONFIRMATION_URL, "123456", "subject", "secret");

        assertThat(allText(mimeMessage)).contains("ada");
    }

    @Test
    @DisplayName("a name that no file matches is reported instead of resolving silently")
    void anUnknownTemplateNameIsReported() {
        assertThatCode(() -> engine.process("NO_SUCH_TEMPLATE", new Context()))
                .isInstanceOf(org.thymeleaf.exceptions.TemplateInputException.class);
    }

    /**
     * The file names as they exist, read from the classpath instead of being
     * resolved by name, so no case-insensitive lookup can mask a mismatch.
     */
    private Set<String> templateFileNames() throws IOException {
        Resource[] found = new PathMatchingResourcePatternResolver().getResources("classpath*:templates/*.html");
        assertThat(found).as("templates found on the classpath").isNotEmpty();
        return Arrays.stream(found)
                .map(Resource::getFilename)
                .collect(Collectors.toSet());
    }

    /** Mirrors the Spring Boot defaults, this application declaring no thymeleaf properties. */
    private static SpringTemplateEngine engineFor(ITemplateResolver resolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    /**
     * Records what EmailService asked for, before any lookup takes place.
     * TemplateEngine.process and AbstractTemplateResolver.resolveTemplate are
     * both final, so the resolver is the only place a request can be observed.
     */
    private static final class RecordingTemplateResolver implements ITemplateResolver {

        private final List<String> requested = new ArrayList<>();

        private final ClassLoaderTemplateResolver delegate = new ClassLoaderTemplateResolver();

        private RecordingTemplateResolver() {
            delegate.setPrefix("templates/");
            delegate.setSuffix(".html");
            delegate.setTemplateMode(TemplateMode.HTML);
            delegate.setCharacterEncoding("UTF-8");
            delegate.setCacheable(false);
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public Integer getOrder() {
            return delegate.getOrder();
        }

        @Override
        public TemplateResolution resolveTemplate(IEngineConfiguration configuration,
                                                 String ownerTemplate,
                                                 String template,
                                                 Map<String, Object> attributes) {
            requested.add(template);
            return delegate.resolveTemplate(configuration, ownerTemplate, template, attributes);
        }
    }

    /** Collects every text part of the message, whatever multipart shape it took. */
    private String allText(MimeMessage message) throws Exception {
        StringBuilder text = new StringBuilder();
        collect(message.getContent(), text);
        return text.toString();
    }

    private void collect(Object part, StringBuilder text) throws Exception {
        if (part instanceof MimeMultipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                collect(multipart.getBodyPart(i).getContent(), text);
            }
        } else if (part instanceof String body) {
            text.append(body);
        }
    }
}
