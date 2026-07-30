package com.nbh.edushare.modules.auth;

import com.nbh.edushare.modules.auth.dto.request.RegisterRequest;
import com.nbh.edushare.modules.auth.dto.response.AccessTokenResponse;
import com.nbh.edushare.modules.auth.dto.response.AuthTokenResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;

public interface AuthService {
    UserSimpleResponse registerUser(RegisterRequest registerRequest);
    AuthTokenResponse login(String usernameOrEmail, String password);
}
