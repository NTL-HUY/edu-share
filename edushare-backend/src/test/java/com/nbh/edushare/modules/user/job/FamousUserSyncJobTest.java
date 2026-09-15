package com.nbh.edushare.modules.user.job;

import com.nbh.edushare.modules.user.dto.response.FollowCountProjection;
import com.nbh.edushare.modules.user.event.UserFamousStatusChangedEvent;
import com.nbh.edushare.modules.user.repository.FollowRepository;
import com.nbh.edushare.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FamousUserSyncJobTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FamousUserSyncJob famousUserSyncJob;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(famousUserSyncJob, "UP_THRESHOLD", 10000L);
        ReflectionTestUtils.setField(famousUserSyncJob, "DOWN_THRESHOLD", 8000L);
    }

    @Test
    void sync_shouldPublishPromoteEvent_whenUserExceedsThreshold() {
        FollowCountProjection projection = mock(FollowCountProjection.class);
        when(projection.getFolloweeId()).thenReturn(1L);
        when(projection.getFollowerCount()).thenReturn(15000L);
        when(followRepository.countFollowersGroupedByFollowee()).thenReturn(List.of(projection));
        when(userRepository.findIdByIsFamousTrue()).thenReturn(Collections.emptyList());

        famousUserSyncJob.sync();

        verify(userRepository).bulkSetFamous(List.of(1L), true);
        verify(eventPublisher).publishEvent(any(UserFamousStatusChangedEvent.class));
    }

    @Test
    void sync_shouldPublishDemoteEvent_whenUserBelowThreshold() {
        FollowCountProjection projection = mock(FollowCountProjection.class);
        when(projection.getFolloweeId()).thenReturn(1L);
        when(projection.getFollowerCount()).thenReturn(5000L);
        when(followRepository.countFollowersGroupedByFollowee()).thenReturn(List.of(projection));
        when(userRepository.findIdByIsFamousTrue()).thenReturn(List.of(1L));

        famousUserSyncJob.sync();

        verify(userRepository).bulkSetFamous(List.of(1L), false);
        verify(eventPublisher).publishEvent(any(UserFamousStatusChangedEvent.class));
    }

    @Test
    void sync_shouldNotPublishEvent_whenNoChanges() {
        FollowCountProjection projection = mock(FollowCountProjection.class);
        when(projection.getFolloweeId()).thenReturn(1L);
        when(projection.getFollowerCount()).thenReturn(9000L);
        when(followRepository.countFollowersGroupedByFollowee()).thenReturn(List.of(projection));
        when(userRepository.findIdByIsFamousTrue()).thenReturn(List.of(1L));

        famousUserSyncJob.sync();

        verify(userRepository, never()).bulkSetFamous(any(), anyBoolean());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
