package com.nbh.edushare.modules.knowledge.dto.request;

import com.nbh.edushare.common.dto.PageableInput;

public record KnowledgeFilterInput(
        Integer number,
        Integer size,
        String sort
) implements PageableInput {
}
