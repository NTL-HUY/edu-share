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

    @PostMapping("/{username}/follow")
    public ResponseEntity<Object> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        followService.followUser(userId, username);
        return ResponseEntity.ok(Map.of("message", "Follow thành công"));
    }

    @DeleteMapping("/{username}/follow")
    public ResponseEntity<Object> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        followService.unfollowUser(userId, username);
        return ResponseEntity.ok(Map.of("message", "Unfollow thành công"));
    }

    @GetMapping("/{username}/follow/status")
    public ResponseEntity<FollowStatusResponse> checkFollowStatus(
            @PathVariable String username,
            @AuthenticationPrincipal Long userId
    ) {
        FollowStatusResponse status = followService.checkFollowStatus(userId, username);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UserSimpleResponse>> getFollowers(
            @PathVariable String username,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserSimpleResponse> followers = followService.getFollowers(username, pageable);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserSimpleResponse>>getFollowing(
            @PathVariable String username,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<UserSimpleResponse>  following = followService.getFollowing(username, pageable);
        return ResponseEntity.ok(following);
    }
}
