package com.nbh.edushare.modules.auth;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.auth.dto.response.AccessTokenResponse;
import com.nbh.edushare.modules.auth.dto.response.AuthTokenResponse;
import com.nbh.edushare.modules.auth.exception.AuthErrorCode;
import com.nbh.edushare.modules.auth.refreshtoken.RefreshTokenRotationResult;
import com.nbh.edushare.modules.auth.refreshtoken.RefreshTokenService;
import com.nbh.edushare.modules.auth.security.AuthenticatedUser;
import com.nbh.edushare.modules.auth.security.JwtService;
import com.nbh.edushare.modules.auth.security.TokenPayload;
import com.nbh.edushare.modules.user.UserService;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public UserSimpleResponse registerUser(RegisterRequest registerRequest) {
       return userService.createUser(authMapper.toCreateUserCommand(registerRequest));
    }

    @Override
    public AuthTokenResponse login(String usernameOrEmail, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail,password)
            );
        }catch (AuthenticationException e){
            log.error("Login failed for user '{}': {} - {}",
                    usernameOrEmail, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new AppException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        return createAuthToken(authenticatedUser);
    }

    public AuthTokenResponse refreshToken(String rawToken){
        RefreshTokenRotationResult result = refreshTokenService.rotateRefreshToken(rawToken);
        UserAuthInfo user = userService.findById(result.userId()).orElseThrow(
                () -> new AppException(AuthErrorCode.REFRESH_TOKEN_USER_NOT_FOUND)
        );

        AccessTokenResponse accessToken = jwtService.generateAccessToken(TokenPayload.from(user));
        return AuthTokenResponse.builder()
                .accessToken(accessToken.getToken())
                .refreshToken(result.newRawToken())
                .accessTokenExpiresIn(accessToken.getExpiresIn())
                .build();
    }

    @Override
    public void logout(Long userId, String refreshToken) {
        this.refreshTokenService.logout(userId, refreshToken);
    }

    private AuthTokenResponse createAuthToken(AuthenticatedUser authenticatedUser) {
        AccessTokenResponse accessToken = jwtService.generateAccessToken(TokenPayload.from(authenticatedUser));
        String refreshToken = refreshTokenService.generateRefreshToken(authenticatedUser.getUserAuthInfo().id());

        return AuthTokenResponse.builder()
                .accessToken(accessToken.getToken())
                .accessTokenExpiresIn(accessToken.getExpiresIn())
                .refreshToken(refreshToken)
                .build();

    }

}
