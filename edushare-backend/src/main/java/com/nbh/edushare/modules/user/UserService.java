package com.nbh.edushare.modules.user;


import com.nbh.edushare.modules.user.dto.command.CreateUserCommand;
import com.nbh.edushare.modules.user.dto.request.UpdateProfileRequest;
import com.nbh.edushare.modules.user.dto.response.ProfileResponse;
import com.nbh.edushare.modules.user.dto.response.UserAuthInfo;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserSimpleResponse createUser(CreateUserCommand command);
    Optional<UserAuthInfo> findByUsernameOrEmail(String usernameOrEmail);

    <T> Optional<T> findProjectedById(Long id, Class<T> type);

    long countFollowers(Long userId);
    List<Long> getFollowerIds(Long userId);
    List<Long> findFamousFolloweeIds(Long userId);
    List<Long> findNormalFolloweeIds(Long userId);


//    ProfileResponse getProfile(String username);

    ProfileResponse getMyProfile(Long currentUserId);

    ProfileResponse getProfileByUsername(String targetUsername, Long currentUserId);

    ProfileResponse updateProfile(Long currentUserId, UpdateProfileRequest request);
}

