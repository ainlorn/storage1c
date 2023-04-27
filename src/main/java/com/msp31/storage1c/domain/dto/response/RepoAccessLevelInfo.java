package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

@Value
public class RepoAccessLevelInfo {
    String roleName;
    boolean canView;
    boolean canCommit;
    boolean canManage;
}
