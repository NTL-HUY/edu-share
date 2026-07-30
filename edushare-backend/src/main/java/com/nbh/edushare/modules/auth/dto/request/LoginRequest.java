package com.nbh.edushare.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String usernameOrEmail,
        @NotBlank
        String password
) {
}