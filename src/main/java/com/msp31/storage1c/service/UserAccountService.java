package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.UserInfo;

public interface UserAccountService {
    UserInfo registerUser(UserRegistrationRequest request);

    UserInfo getCurrentUserInfo();
}
