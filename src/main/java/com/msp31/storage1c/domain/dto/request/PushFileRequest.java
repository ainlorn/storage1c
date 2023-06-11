package com.msp31.storage1c.domain.dto.request;

import com.msp31.storage1c.common.constant.RegexConstants;
import com.msp31.storage1c.common.exception.InvalidTagListException;
import com.msp31.storage1c.common.validation.constraint.ValidPath;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public class PushFileRequest {
    long repoId;

    @ValidPath
    @NotEmpty
    String path;

    @NotEmpty
    String message;

    String fileDescription;

    Set<String> fileTags;

    Set<String> commitTags;

    @NotNull
    InputStream fileStream;

    public PushFileRequest(long repoId,
                           String path,
                           String message,
                           String fileDescription,
                           String fileTags,
                           String commitTags,
                           InputStream fileStream) {
        this.repoId = repoId;
        this.path = path;
        this.message = message;
        this.fileDescription = fileDescription;
        this.fileTags = convertTags(fileTags);
        this.commitTags = convertTags(commitTags);
        this.fileStream = fileStream;
    }

    private static final Pattern TAG_PATTERN = Pattern.compile(RegexConstants.TAG_REGEX);

    private Set<String> convertTags(String tags) {
        if (tags == null)
            return null;

        var split = tags.split(";");
        var list = new HashSet<String>();
        for (var tag : split) {
            tag = tag.trim();
            if (!TAG_PATTERN.matcher(tag).matches())
                throw new InvalidTagListException();

            list.add(tag);
        }

        return list;
    }
}
