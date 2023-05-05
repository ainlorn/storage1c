package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;

@Value
public class PublicUserInfo {
    long id;
    String username;
    String fullName;
    Calendar createdOn;
}
