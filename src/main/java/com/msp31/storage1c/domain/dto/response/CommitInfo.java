package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;

@Value
public class CommitInfo {
    public String id;
    public String message;
    public PublicUserInfo author;
    public Calendar when;
}
