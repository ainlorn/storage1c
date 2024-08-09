package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.constant.RegexConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class TagRequest {
    @Length(min=1, max=255)
    @Pattern(regexp = RegexConstants.TAG_REGEX)
    @NotEmpty
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag.trim();
    }
}
