package com.nbh.edushare.modules.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileChangedEvent {
    private final Long userId;
    private final String fullName;
    private final String avatarUrl;
}