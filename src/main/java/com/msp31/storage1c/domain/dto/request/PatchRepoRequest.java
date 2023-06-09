package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidRepoName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

@Value
public class PatchRepoRequest {
    @ValidRepoName
    String repoName;

    @Length(min=0, max=65536)
    String description;

    Boolean isPrivate;
}
