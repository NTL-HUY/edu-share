package com.nbh.edushare.modules.feed.dto.request;

import com.nbh.edushare.modules.knowledge.enums.KnowledgeType;
import com.nbh.edushare.modules.knowledge.enums.LessonLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record FeedSearchInput(
        String keyword,
        KnowledgeType type,
        Long categoryId,
        LessonLevel level,
        @Min(0)
        Integer page,
        @Min(1) @Max(50)
        Integer size,
        String sort
) {
    public int pageOrDefault() { return page != null && page >= 0 ? page : 0; }
    public int sizeOrDefault() { return size != null && size > 0 && size <= 50 ? size : 20; }
    public Sort sortOrDefault() {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").descending();
        }

        String[] parts = sort.split(",");
        String property = parts[0].trim();
        boolean isDesc = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc");

        return isDesc ? Sort.by(property).descending() : Sort.by(property).ascending();
    }

    public Pageable toPageable() {
        return PageRequest.of(pageOrDefault(), sizeOrDefault(), sortOrDefault());
    }
}
