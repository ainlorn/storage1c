package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

@Value
public class RepoInfoResponse {
    RepoInfo repo;
    RepoAccessLevelInfo myAccessLevel;
}
