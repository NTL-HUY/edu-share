package com.nbh.edushare.modules.user;

import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.user.dto.response.FollowStatusResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import com.nbh.edushare.modules.user.pojo.Follow;
import com.nbh.edushare.modules.user.pojo.User;
import com.nbh.edushare.modules.user.repository.FollowRepository;
import com.nbh.edushare.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FollowSerivceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(Long currentUserId, String targetUsername) {
        User targetUser = userRepository.findByUsernameOrEmail(targetUsername, User.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        if (targetUser.getId().equals(currentUserId)) {
            throw new AppException(UserErrorCode.CANNOT_FOLLOW_SELF);
        }

        boolean alreadyFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, targetUser.getId());
        if (alreadyFollowing) {
            throw new AppException(UserErrorCode.ALREADY_FOLLOWING);
        }

        User currentUser = userRepository.getReferenceById(currentUserId);

        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowee(targetUser);

        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(Long currentUserId, String targetUsername) {
        User targetUser = userRepository.findByUsernameOrEmail(targetUsername, User.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        Follow follow = followRepository.findByFollowerIdAndFolloweeId(currentUserId, targetUser.getId())
                .orElseThrow(() -> new AppException(UserErrorCode.NOT_FOLLOWING_YET));

        followRepository.delete(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public FollowStatusResponse checkFollowStatus(Long currentUserId, String targetUsername) {
        User targetUser = userRepository.findByUsernameOrEmail(targetUsername, User.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, targetUser.getId());
        return new FollowStatusResponse(isFollowing);
    }

    @Override
    public Page<UserSimpleResponse> getFollowers(String username, Pageable pageable) {
        User user = userRepository.findByUsernameOrEmail(username, User.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        return followRepository.findFollowersByUserId(user.getId(), pageable)
                .map(userMapper::toUserSimpleResponse);
    }

    @Override
    public Page<UserSimpleResponse> getFollowing(String username, Pageable pageable) {
        User user = userRepository.findByUsernameOrEmail(username, User.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));

        return followRepository.findFollowingByUserId(user.getId(), pageable)
                .map(userMapper::toUserSimpleResponse);
    }
}
