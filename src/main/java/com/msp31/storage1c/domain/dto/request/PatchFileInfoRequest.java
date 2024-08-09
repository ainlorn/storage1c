package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidTagSet;
import com.msp31.storage1c.utils.ListTrimmer;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class PatchFileInfoRequest {
    @Size(min=0, max=65536)
    String description;

    @ValidTagSet
    Set<String> tags;

    public PatchFileInfoRequest(String description, List<String> tags) {
        this.description = description;
        this.tags = ListTrimmer.trimToSet(tags);
    }
}
