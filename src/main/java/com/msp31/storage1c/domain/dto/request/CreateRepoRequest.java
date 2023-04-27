package com.msp31.storage1c.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class CreateRepoRequest {

    @Length(min=3, max=255)
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я-_ ]*$")
    @NotEmpty
    String repoName;

    @NotNull
    Boolean isPrivate;
}
