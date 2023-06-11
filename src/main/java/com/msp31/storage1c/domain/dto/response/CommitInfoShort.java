package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.Calendar;

@Value
public class CommitInfoShort {
    String id;
    String message;
    Calendar when;
}
