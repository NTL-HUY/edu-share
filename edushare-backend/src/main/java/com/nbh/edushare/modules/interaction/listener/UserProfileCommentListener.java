package com.nbh.edushare.modules.interaction.listener;

import com.nbh.edushare.modules.interaction.repository.CommentRepository;
import com.nbh.edushare.modules.user.event.UserProfileChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserProfileCommentListener {
    private final CommentRepository commentRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserProfileChanged(UserProfileChangedEvent event) {
        int updated = commentRepository.syncUserInfo(
                event.getUserId(), event.getFullName(), event.getAvatarUrl());
        log.info("Synced comment user info: userId={}, rows={}",
                event.getUserId(), updated);
    }
}