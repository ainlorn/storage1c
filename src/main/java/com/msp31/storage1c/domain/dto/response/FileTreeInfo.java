package com.msp31.storage1c.domain.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class FileTreeInfo {
    List<FileTreeInfo.File> files;

    @Value
    public static class File {
        String name;
        String type;
        List<FileTreeInfo.File> files;
        CommitInfoShort lastCommit;
    }
}
