package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.AsciiString;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class UserAuthenticationRequest {
    @Length(min=3, max=255)
    @AsciiString
    @NotEmpty
    String username;

    @Length(min=8, max=72)
    @NotEmpty
    String password;
}
