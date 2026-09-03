package com.nbh.edushare.modules.knowledge.dto.command;

public record CreateCourseCommand(
        String title,
        String abstractText,
        String description,
        String coverImage,
        Long category_id,
        Integer estimateTimeInMinutes,
        Long ownerId
) {}