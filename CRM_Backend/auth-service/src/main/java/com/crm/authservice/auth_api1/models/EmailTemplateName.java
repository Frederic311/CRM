package com.crm.authservice.auth_api1.models;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset_password"),
    PASSWORD_RESET_CONFIRMATION("PASSWORD_RESET_CONFIRMATION");

    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }
}
