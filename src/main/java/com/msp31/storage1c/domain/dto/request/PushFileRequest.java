package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidPath;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.InputStream;

@Value
public class PushFileRequest {
    long repoId;

    @ValidPath
    @NotEmpty
    String path;

    @NotEmpty
    String message;

    @NotNull
    InputStream fileStream;
}
