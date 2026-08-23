package com.nbh.edushare.modules.user;

import com.nbh.edushare.common.dto.ApiResponse;
import com.nbh.edushare.common.exception.AppException;
import com.nbh.edushare.modules.user.dto.request.UpdateProfileRequest;
import com.nbh.edushare.modules.user.dto.response.ProfileResponse;
import com.nbh.edushare.modules.user.dto.response.UserBaseProjection;
import com.nbh.edushare.modules.user.exception.UserErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getProfile(username));
    }

    @GetMapping("/me")
    public ResponseEntity<UserBaseProjection> getMe(@AuthenticationPrincipal Long userId) {
        UserBaseProjection me = userService.findProjectedById(userId, UserBaseProjection.class)
                .orElseThrow(() -> new AppException(UserErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(me);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ProfileResponse>  updateMyProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
}
