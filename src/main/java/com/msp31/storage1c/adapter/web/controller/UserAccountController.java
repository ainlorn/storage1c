package com.msp31.storage1c.adapter.web.controller;

import com.msp31.storage1c.adapter.web.annotation.ApiV1;
import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.ResponseModel;
import com.msp31.storage1c.domain.dto.response.UserInfoResponse;
import com.msp31.storage1c.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.msp31.storage1c.domain.dto.response.ResponseModel.ok;

@ApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAccountController {

    UserAccountService userAccountService;

    @PostMapping("/register")
    public ResponseModel<UserInfoResponse> registerAccount(@Valid @RequestBody UserRegistrationRequest request) {
        return ok(userAccountService.registerUser(request));
    }

}
