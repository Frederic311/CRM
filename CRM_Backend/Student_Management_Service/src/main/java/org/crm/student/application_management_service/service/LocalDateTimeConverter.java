package org.crm.student.application_management_service.service;

import com.opencsv.bean.AbstractBeanField;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends AbstractBeanField<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    protected LocalDateTime convert(String value) {
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing LocalDateTime: " + value, e);
        }
    }
}
