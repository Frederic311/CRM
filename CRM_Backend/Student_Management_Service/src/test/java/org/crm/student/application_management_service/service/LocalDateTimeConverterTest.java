package org.crm.student.application_management_service.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalDateTimeConverterTest {

    private static class TestableConverter extends LocalDateTimeConverter {
        LocalDateTime convertValue(String value) {
            return super.convert(value);
        }
    }

    @Test
    void convert_shouldParseIsoDateTime() {
        TestableConverter converter = new TestableConverter();

        LocalDateTime value = converter.convertValue("2026-04-04T12:30:45");

        assertEquals(LocalDateTime.of(2026, 4, 4, 12, 30, 45), value);
    }

    @Test
    void convert_shouldThrowRuntimeExceptionForInvalidDate() {
        TestableConverter converter = new TestableConverter();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> converter.convertValue("not-a-date"));

        assertEquals(true, ex.getMessage().contains("Error parsing LocalDateTime"));
    }
}
