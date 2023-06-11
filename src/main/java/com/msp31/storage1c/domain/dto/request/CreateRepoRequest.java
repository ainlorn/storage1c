package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidRepoName;
import com.msp31.storage1c.common.validation.constraint.ValidTagSet;
import com.msp31.storage1c.utils.ListTrimmer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Value;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Set;

@Value
public class CreateRepoRequest {
    @ValidRepoName
    @NotEmpty
    String repoName;

    @Length(min=0, max=65536)
    String description;

    @ValidTagSet
    Set<String> tags;

    @NotNull
    Boolean isPrivate;

    public CreateRepoRequest(String repoName, String description, List<String> tags, Boolean isPrivate) {
        this.repoName = repoName;
        this.description = description;
        this.tags = ListTrimmer.trimToSet(tags);
        this.isPrivate = isPrivate;
    }
}
