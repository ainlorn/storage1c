package com.msp31.storage1c.module.git;

import lombok.Value;

@Value
public class GitFile {
    public static final String TYPE_FILE = "file";
    public static final String TYPE_DIRECTORY = "directory";

    String name;
    String type;
    String blobId;
}
