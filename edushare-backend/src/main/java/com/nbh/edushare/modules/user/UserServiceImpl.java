package com.nbh.edushare.modules.user;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.enums.UserRole;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserSimpleResponse createUser(CreateUserCommand command) {
        Optional<User> existingUser = userRepository.findByUsernameOrEmail(
                command.getUsername(), command.getEmail()
        );
        if (existingUser.isPresent()) {
            User foundUser = existingUser.get();
            if(foundUser.getUsername().equals(command.getUsername())) {
                throw new AppException(UserErrorCode.USERNAME_ALREADY_EXISTS);
            }
            throw new AppException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toUser(command);
        user.setPasswordHash(passwordEncoder.encode(command.getPassword()));
        user.setUserRole(UserRole.USER);
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(UserErrorCode.USERNAME_OR_EMAIL_ALREADY_EXISTS);
        }

        Profile profile = new Profile();
        profile.setUser(user);
        profileRepository.save(profile);

        return userMapper.toUserSimpleResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAuthInfo> findByUsernameOrEmail(String usernameOrEmail) {
        return  this.userRepository.findByUsernameOrEmail(usernameOrEmail)
                .map(userMapper::toUserAuthInfo);


//        java
//        Optional<User> userOpt = userRepository.findByUsernameOrEmail(usernameOrEmail);
//
//        if (userOpt.isPresent()) {
//            User user = userOpt.get();
//            UserAuthInfo info = userMapper.toUserAuthInfo(user);  // ← đây, vẫn gọi userMapper
//            return Optional.of(info);
//        } else {
//            return Optional.empty();
//        }
    }


}
