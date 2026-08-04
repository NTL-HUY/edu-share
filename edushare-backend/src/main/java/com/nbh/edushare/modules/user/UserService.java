package com.nbh.edushare.modules.user;


import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;

import java.util.Optional;

public interface UserService {
    UserSimpleResponse createUser(CreateUserCommand command);
    Optional<UserAuthInfo> findByUsernameOrEmail(String usernameOrEmail);
    Optional<UserAuthInfo> findById(Long id);
}
