package com.nbh.edushare.modules.user;

import com.nbh.edushare.modules.auth.security.AuthenticatedUser;
import com.nbh.edushare.modules.user.dto.response.FollowStatusResponse;
import com.nbh.edushare.modules.user.dto.response.UserSimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 1. Follow a user
    @PostMapping("/{username}/follow")
    public ResponseEntity<Object> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        followService.followUser(userId, username);
        return ResponseEntity.ok(Map.of("message", "Follow thành công"));
    }

    // 2. Unfollow a user
    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Object> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        followService.unfollowUser(userId, username);
        return ResponseEntity.ok(Map.of("message", "Unfollow thành công"));
    }

    // 3. Check status (đang follow hay chưa)
    @GetMapping("/{username}/follow/status")
    public ResponseEntity<FollowStatusResponse> checkFollowStatus(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        FollowStatusResponse status = followService.checkFollowStatus(userId, username);
        return ResponseEntity.ok(status);
    }

    // 4. Lấy danh sách Followers (Public API)
    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserSimpleResponse>> getFollowers(
            @PathVariable String username,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserSimpleResponse> followers = followService.getFollowers(username, pageable);
        return ResponseEntity.ok(followers);
    }

    // 5. Lấy danh sách Following (Public API)
    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserSimpleResponse>>getFollowing(
            @PathVariable String username,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserSimpleResponse>  following = followService.getFollowing(username, pageable);
        return ResponseEntity.ok(following);
    }
}
