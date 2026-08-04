package com.nbh.edushare.modules.auth;

import com.nbh.edushare.common.dto.ApiResponse;
import com.nbh.edushare.modules.auth.dto.request.AuthLogoutRequest;
import com.nbh.edushare.modules.auth.dto.request.AuthRefreshRequest;
import com.nbh.edushare.modules.auth.dto.request.LoginRequest;
import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.auth.dto.response.AuthTokenResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<UserSimpleResponse> registerUser(@Valid @RequestBody RegisterRequest request){
        UserSimpleResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody @Valid LoginRequest request) {
        String usernameOrEmail = request.usernameOrEmail();
        String password = request.password();

        return ResponseEntity.ok(authService.login(usernameOrEmail, password));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@RequestBody @Valid AuthRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.token()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid AuthLogoutRequest request) {

        authService.logout(userId, request.refreshToken());

        return ResponseEntity.ok(new ApiResponse("Logout successfully", null));
    }

}
