package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;

@Value
public class UserInfo {
    long id;
    String username;
    String fullName;
    String email;
    long roleId;
    Calendar createdOn;
    boolean enabled;
}
