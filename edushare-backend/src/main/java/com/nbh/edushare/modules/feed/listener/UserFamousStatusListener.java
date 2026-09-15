package com.nbh.edushare.modules.feed.listener;

import com.nbh.edushare.modules.feed.FeedProjectionService;
import com.nbh.edushare.modules.user.event.UserFamousStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFamousStatusListener {
    private final FeedProjectionService  feedProjectionService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFamousStatusChanged(UserFamousStatusChangedEvent event) {
        if (event.isFamous()) {
            feedProjectionService.handleFamousPromotedUser(event.getUserIds());
        }
        log.info("Handled famous status change: userIds={}, famous={}",
                event.getUserIds(), event.isFamous());
    }

}
