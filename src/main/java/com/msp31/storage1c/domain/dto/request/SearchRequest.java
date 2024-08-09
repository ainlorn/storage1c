package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidTagSet;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.Set;

@Value
public class SearchRequest {
    @ValidTagSet
    @NotNull
    @Size(min = 1, max = 10)
    Set<String> tags;
}
