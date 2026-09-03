package com.nbh.edushare.modules.knowledge.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LessonLevel {
    BEGINNER("Sơ cấp"),
    INTERMEDIATE("Trung cấp"),
    ADVANCED("Nâng cao");

    private final String displayName;
}