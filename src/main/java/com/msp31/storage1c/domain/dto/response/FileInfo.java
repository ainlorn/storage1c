package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class FileInfo {
    String name;
    String type;
    List<FileInfo> files;
    CommitInfo lastCommit;
}
