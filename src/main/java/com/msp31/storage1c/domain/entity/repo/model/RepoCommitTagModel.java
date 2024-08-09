package com.msp31.storage1c.domain.entity.repo.model;

import com.msp31.storage1c.domain.entity.repo.RepoCommit;
import lombok.Value;

@Value
public class RepoCommitTagModel {
    RepoCommit commit;
    String tag;
}
