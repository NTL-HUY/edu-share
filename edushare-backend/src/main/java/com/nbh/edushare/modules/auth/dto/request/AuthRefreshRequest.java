package com.nbh.edushare.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;


public record AuthRefreshRequest(
        @NotBlank
        String token
) {
}