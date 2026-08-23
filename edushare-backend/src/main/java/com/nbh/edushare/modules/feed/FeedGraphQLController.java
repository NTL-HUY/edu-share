package com.nbh.edushare.modules.feed;

import com.nbh.edushare.modules.feed.dto.request.FeedQueryInput;
import com.nbh.edushare.modules.feed.dto.request.FeedSearchInput;
import com.nbh.edushare.modules.feed.dto.response.FeedPage;
import com.nbh.edushare.modules.feed.dto.response.FeedSearchResult;
import com.nbh.edushare.modules.feed.pojo.FeedItem;
import com.nbh.edushare.modules.feed.repository.FeedItemRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class FeedGraphQLController {
    private final FeedService feedService;
    private final FeedMapper feedMapper;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public FeedPage getFeed(@Argument FeedQueryInput input,
                            @AuthenticationPrincipal Long userId
    ) {
        FeedQueryInput queryInput = (input != null) ? input : new FeedQueryInput(null, 20);

        return feedService.getFeed(
                userId,
                queryInput.cursor(),
                queryInput.getSafeLimit()
        );
    }

    @QueryMapping
    public FeedSearchResult searchFeed(@Valid @Argument FeedSearchInput input) {
        return feedService.searchFeed(input,input.toPageable());
    }

    @SchemaMapping(typeName = "FeedItem", field = "typeMeta")
    public Object getTypeMeta(FeedItem feedItem) {
        return feedMapper.mapJsonNodeToTypeMeta(feedItem);
    }
}
