package com.nbh.edushare.modules.user.job;

import com.nbh.edushare.modules.user.dto.response.FollowCountProjection;
import com.nbh.edushare.modules.user.event.UserFamousStatusChangedEvent;
import com.nbh.edushare.modules.user.repository.FollowRepository;
import com.nbh.edushare.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class FamousUserSyncJob {
    @Value("${app.user.famous.up-threshold:10000}")
    private long UP_THRESHOLD;

    @Value("${app.user.down-threshold:8000}")
    private long DOWN_THRESHOLD;

    private final FollowRepository followRepository;
    private  final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(initialDelayString = "${app.job.user.famous-sycn-init-delay}", fixedDelayString = "${app.job.user.famous-sync-cron}")
    @Transactional
    public void sync(){
        Map<Long,Long> followerCounts = followRepository.countFollowersGroupedByFollowee().stream()
                .collect(Collectors.toMap(
                        FollowCountProjection::getFolloweeId,
                        FollowCountProjection::getFollowerCount
                ));

        List<Long> currentFamousIds = userRepository.findIdByIsFamousTrue();
        Set<Long> currentFamousSet = new HashSet<>(currentFamousIds);

        List<Long> toPromote = new ArrayList<>();
        List<Long> toDemote = new ArrayList<>();

        followerCounts.forEach((userId, count) -> {
            boolean isCurrentlyFamous = currentFamousSet.contains(userId);
            if (!isCurrentlyFamous && count >= UP_THRESHOLD) {
                toPromote.add(userId);
            }
        });

        for (Long famousId : currentFamousIds) {
            long count = followerCounts.getOrDefault(famousId, 0L);
            if (count < DOWN_THRESHOLD) {
                toDemote.add(famousId);
            }
        }

        if (!toPromote.isEmpty()) {
            userRepository.bulkSetFamous(toPromote, true);
            eventPublisher.publishEvent(new UserFamousStatusChangedEvent(toPromote, true));
        }

        if (!toDemote.isEmpty()) {
            userRepository.bulkSetFamous(toDemote, false);
            eventPublisher.publishEvent(new UserFamousStatusChangedEvent(toDemote, false));
        }

        log.info("FamousUserSyncJob: promoted={}, demoted={}", toPromote.size(), toDemote.size());
    }


}
