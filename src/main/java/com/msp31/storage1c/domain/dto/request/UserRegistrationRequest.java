package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.AsciiString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class UserRegistrationRequest {
    @Length(min=3, max=63)
    @AsciiString
    @Pattern(regexp = "^[a-zA-Z0-9_-]*$")
    @NotEmpty
    String username;

    @Length(min=3, max=255)
    @Email
    @AsciiString
    @NotEmpty
    String email;

    @Length(min=3, max=100)
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я- ]*$")
    @NotEmpty
    String fullName;

    @Length(min=8, max=72)
    @NotEmpty
    String password;
}
