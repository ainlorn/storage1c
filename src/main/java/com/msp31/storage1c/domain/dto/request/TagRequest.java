package com.msp31.storage1c.domain.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public class TagRequest {
    @Length(min=1, max=255)
    @Pattern(regexp = "^[a-zA-Z0-9а-яА-Я-_(): ]*$")
    @NotEmpty
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag.trim();
    }
}
