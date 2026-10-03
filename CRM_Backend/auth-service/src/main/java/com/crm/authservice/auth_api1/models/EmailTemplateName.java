package com.crm.authservice.auth_api1.models;

import lombok.Getter;

/**
 * Names of the Thymeleaf templates in src/main/resources/templates.
 *
 * The string carried by each constant is what Thymeleaf resolves against the
 * file name, so it has to match it exactly. All three are lower_snake_case
 * because that is the convention the templates now follow.
 *
 * EmailService previously called name(), which returns the constant name and
 * not this field, so ACTIVATE_ACCOUNT resolved to ACTIVATE_ACCOUNT.html while
 * the file is activate_account.html.
 */
@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"),
    PASSWORD_RESET_CONFIRMATION("password_reset_confirmation");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
