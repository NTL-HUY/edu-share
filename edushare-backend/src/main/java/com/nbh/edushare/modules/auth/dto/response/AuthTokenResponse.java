package com.nbh.edushare.modules.auth.dto.response;


import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
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
    private long refreshTokenExpiresIn;
    @Builder.Default
    private String accessTokenType = "Bearer";
}
