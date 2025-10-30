package com.crm.authservice.auth_api1.Response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private String message;
    private boolean requirePasswordChange;
}


