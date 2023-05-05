package com.msp31.storage1c.domain.mapper;

import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.PublicUserInfo;
import com.msp31.storage1c.domain.dto.response.UserInfo;
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
                dto.getEmail().toLowerCase(),
                passwordEncoder.encode(dto.getPassword()),
                0,
                true
        );
    }

    public UserInfo createUserInfoFrom(User user) {
        return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedOn(),
                user.getEnabled()
        );
    }

    public PublicUserInfo createPublicUserInfoFrom(User user) {
        return new PublicUserInfo(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getCreatedOn()
        );
    }
}
