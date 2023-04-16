package com.msp31.storage1c.domain.entity.account.model;

import lombok.Value;

@Value
public class UserModel {

    String username;

    String fullName;

    String email;

    String password;

    long role;

    boolean enabled;
}
