package com.nbh.edushare.modules.interaction.counter;


import com.nbh.edushare.modules.interaction.event.CommentChangedEvent;
import com.nbh.edushare.modules.interaction.event.ViewRecordedEvent;
import com.nbh.edushare.modules.interaction.event.VoteChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterListener {
    private final CounterService counterService;
    @EventListener
    public void handleVoteEvent(VoteChangedEvent event) {
        counterService.incrVote(event.knowledgeId(), event.delta());
    }

    @EventListener
    public void handleCommentEvent(CommentChangedEvent event) {
        counterService.incrComment(event.knowledgeId(), event.delta());
    }

    @EventListener
    public void handleViewEvent(ViewRecordedEvent event) {
        counterService.incrView(event.knowledgeId());
    }
}
