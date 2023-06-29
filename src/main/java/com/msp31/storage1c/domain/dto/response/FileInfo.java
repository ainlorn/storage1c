package com.msp31.storage1c.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;

import java.util.List;

@Value
public class FileInfo {
    String name;
    String type;
    String description;
    FileDownloadInfo download;
    List<String> tags;
    FileInfo.Lock lock;

    @Value
    public static class Lock {
        boolean locked;
        @JsonInclude
        PublicUserInfo user;
    }
}
