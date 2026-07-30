package com.nbh.edushare.modules.auth.dto.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthTokenResponse {
    private String refreshToken;
    private String accessToken;
    private long accessTokenExpiresIn;
    @Builder.Default
    private String accessTokenType = "Bearer";
}
