package com.nbh.edushare.modules.auth.refreshtoken;

import java.time.Instant;

public record GeneratedRefreshToken(
        String rawToken,
        Instant expiresAt
) {}