package com.nbh.edushare.modules.user;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.request.UpdateProfileRequest;
import com.nbh.edushare.modules.user.dto.response.ProfileResponse;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.enums.UserRole;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import com.nbh.edushare.modules.user.pojo.Profile;
import com.nbh.edushare.modules.user.pojo.User;
import com.nbh.edushare.modules.user.repository.FollowRepository;
import com.nbh.edushare.modules.user.repository.ProfileRepository;
import com.nbh.edushare.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final FollowRepository followRepository;
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
        return userRepository.findByUsernameOrEmail(usernameOrEmail, UserAuthInfo.class);



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

    @Override
    public <T> Optional<T> findProjectedById(Long id, Class<T> type) {
        return userRepository.findProjectedById(id, type);
    }

    @Override
    public long countFollowers(Long userId) {
        return followRepository.countByFollowee_Id(userId);
    }

    @Override
    public List<Long> getFollowerIds(Long userId) {
        return followRepository.findFollowersIdByFollowee_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findFamousFolloweeIds(Long userId) {
        return followRepository.findFamousFolloweeIds(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findNormalFolloweeIds(Long userId) {
        return followRepository.findNormalFolloweeIds(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(UserErrorCode.PROFILE_NOT_FOUND));
        return userMapper.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(UserErrorCode.PROFILE_NOT_FOUND));

        userMapper.updateProfileFromRequest(request, profile);

        return userMapper.toProfileResponse(profile);
    }

}
