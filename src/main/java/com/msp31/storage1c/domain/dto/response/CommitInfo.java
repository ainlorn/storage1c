package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;
import java.util.List;

@Value
public class CommitInfo {
    String id;
    String message;
    List<String> tags;
    PublicUserInfo author;
    Calendar when;
}
