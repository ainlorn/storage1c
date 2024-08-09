package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;
import java.util.List;

@Value
public class RepoInfo {
    long id;
    String name;
    String description;
    List<String> tags;
    PublicUserInfo owner;
    boolean isPublic;
    Calendar createdOn;
}
