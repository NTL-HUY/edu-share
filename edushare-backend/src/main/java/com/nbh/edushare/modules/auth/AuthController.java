package com.nbh.edushare.modules.auth;

import com.nbh.edushare.modules.auth.dto.request.LoginRequest;
import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.auth.dto.response.AuthTokenResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
