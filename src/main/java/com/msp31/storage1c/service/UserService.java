package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.PublicUserInfo;
import com.msp31.storage1c.domain.dto.response.UserInfo;

public interface UserService {
    UserInfo registerUser(UserRegistrationRequest request);

    UserInfo getCurrentUserInfo();

    PublicUserInfo getPublicUserInfo(long userId);

    long getUserIdByUsername(String username);
}
