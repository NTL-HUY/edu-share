package com.nbh.edushare.modules.interaction.dto.request;

import com.nbh.edushare.common.dto.PageableInput;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageSearchInput(
        @Min(0) Integer number,
        @Min(1) @Max(50) Integer size,
        String sort
) implements PageableInput {}
