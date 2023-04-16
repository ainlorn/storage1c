package com.msp31.storage1c.domain.mapper;

import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.UserInfoResponse;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.account.model.UserModel;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {

    PasswordEncoder passwordEncoder;

    public UserModel createModelFrom(UserRegistrationRequest dto) {
        return new UserModel(
                dto.getUsername(),
                dto.getFullName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                0,
                true
        );
    }

    public UserInfoResponse createUserInfoResponseFrom(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedOn(),
                user.getEnabled()
        );
    }
}
