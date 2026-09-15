package com.nbh.edushare.modules.user.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class UserFamousStatusChangedEvent {
    private final Collection<Long> userIds;
    private final boolean isFamous;
}