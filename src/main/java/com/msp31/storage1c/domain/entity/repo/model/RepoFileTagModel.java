package com.msp31.storage1c.domain.entity.repo.model;

import com.msp31.storage1c.domain.entity.repo.RepoFile;
import lombok.Value;

@Value
public class RepoFileTagModel {
    RepoFile file;
    String tag;
}
