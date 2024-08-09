package com.msp31.storage1c.domain.entity.repo.model;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.RepoFile;
import lombok.Value;

@Value
public class RepoFileLockModel {
    RepoFile file;
    User user;
}
