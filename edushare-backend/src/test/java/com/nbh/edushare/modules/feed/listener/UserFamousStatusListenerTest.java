package com.nbh.edushare.modules.feed.listener;

import com.nbh.edushare.modules.feed.FeedProjectionService;
import com.nbh.edushare.modules.user.event.UserFamousStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFamousStatusListenerTest {

    @Mock
    private FeedProjectionService feedProjectionService;

    @InjectMocks
    private UserFamousStatusListener listener;

    @Test
    void onFamousStatusChanged_shouldCallHandleFamousPromotedUser_whenFamous() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        UserFamousStatusChangedEvent event = new UserFamousStatusChangedEvent(userIds, true);

        listener.onFamousStatusChanged(event);

        verify(feedProjectionService).handleFamousPromotedUser(userIds);
    }

    @Test
    void onFamousStatusChanged_shouldNotCallHandleFamousPromotedUser_whenNotFamous() {
        List<Long> userIds = List.of(1L, 2L);
        UserFamousStatusChangedEvent event = new UserFamousStatusChangedEvent(userIds, false);

        listener.onFamousStatusChanged(event);

        verify(feedProjectionService, never()).handleFamousPromotedUser(any());
    }
}
