package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.AsciiString;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class AddUserToRepoRequest {
    Long userId;

    String username;

    @NotEmpty
    @AsciiString
    String role;
}
