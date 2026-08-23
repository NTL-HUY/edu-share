package com.nbh.edushare.modules.media.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MediaFolder {
    AVATAR("avatars"),
    LESSON_THUMBNAIL("knowledge/thumbnails"),
    CV("cvs"),
    COURSE_COVER("knowledge/covers");
    private final String path;
}