package com.msp31.storage1c.service;

import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.UserInfoResponse;

public interface UserAccountService {
    UserInfoResponse registerUser(UserRegistrationRequest request);

    UserInfoResponse getUserInfo(String username);
}
