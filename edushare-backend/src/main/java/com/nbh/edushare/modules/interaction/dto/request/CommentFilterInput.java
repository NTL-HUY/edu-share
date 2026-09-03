package com.nbh.edushare.modules.interaction.dto.request;

import com.nbh.edushare.common.dto.PageableInput;

public record CommentFilterInput(
        Integer number,
        Integer size,
        String sort
) implements PageableInput { }