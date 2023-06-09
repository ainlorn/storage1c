package com.msp31.storage1c.domain.entity.repo.model;

import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.repo.RepoAccessLevel;
import lombok.Value;

@Value
public class RepoModel {

    String name;

    String description;

    String directoryName;

    User owner;

    RepoAccessLevel defaultAccessLevel;
}
