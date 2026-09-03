package com.nbh.edushare.modules.user;

import com.nbh.edushare.modules.user.dto.response.FollowStatusResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowService {
    void followUser(Long currentUserId, String targetUsername);
    void unfollowUser(Long currentUserId, String targetUsername);
    FollowStatusResponse checkFollowStatus(Long currentUserId, String targetUsername);
    Page<UserSimpleResponse> getFollowers(String username, Pageable pageable);
    Page<UserSimpleResponse> getFollowing(String username, Pageable pageable);
}
