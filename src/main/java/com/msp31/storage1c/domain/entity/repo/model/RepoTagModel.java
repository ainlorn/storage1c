package com.msp31.storage1c.domain.entity.repo.model;

import com.msp31.storage1c.domain.entity.repo.Repo;
import lombok.Value;

@Value
public class RepoTagModel {
    Repo repo;
    String tag;
}
