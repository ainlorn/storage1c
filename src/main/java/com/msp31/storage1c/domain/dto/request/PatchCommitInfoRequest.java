package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.validation.constraint.ValidTagSet;
import com.msp31.storage1c.utils.ListTrimmer;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
public class PatchCommitInfoRequest {
    @ValidTagSet
    Set<String> tags;

    public PatchCommitInfoRequest(List<String> tags) {
        this.tags = ListTrimmer.trimToSet(tags);
    }
}
