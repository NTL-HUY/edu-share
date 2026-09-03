package com.nbh.edushare.modules.interaction.dto.request;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull Short value
) {}
