package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.UserRepository;
import com.msp31.storage1c.common.exception.EmailInUseException;
import com.msp31.storage1c.common.exception.UserNotFoundException;
import com.msp31.storage1c.common.exception.UsernameInUseException;
import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.UserInfoResponse;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.account.model.UserModel;
import com.msp31.storage1c.domain.mapper.UserMapper;
import com.msp31.storage1c.service.UserAccountService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    UserMapper userMapper;
    UserRepository userRepository;

    @Override
    public UserInfoResponse registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new UsernameInUseException();
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailInUseException();

        UserModel model = userMapper.createModelFrom(request);
        User user = User.createFromModel(model);
        user = userRepository.save(user);
        return userMapper.createUserInfoResponseFrom(user);
    }

    @Override
    public UserInfoResponse getUserInfo(String username) {
        var user = userRepository.getByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException();

        return userMapper.createUserInfoResponseFrom(user.get());
    }
}
