package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;
import java.util.List;

@Value
public class RepoInfo {
    long id;
    String name;
    PublicUserInfo owner;
    boolean isPublic;
    Calendar createdOn;
    List<RepoUserAccessInfo> users;
}
