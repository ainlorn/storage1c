package com.msp31.storage1c.service.impl;

import com.msp31.storage1c.adapter.repository.UserRepository;
import com.msp31.storage1c.common.exception.EmailInUseException;
import com.msp31.storage1c.common.exception.UserNotFoundException;
import com.msp31.storage1c.common.exception.UsernameInUseException;
import com.msp31.storage1c.domain.dto.request.UserRegistrationRequest;
import com.msp31.storage1c.domain.dto.response.PublicUserInfo;
import com.msp31.storage1c.domain.dto.response.UserInfo;
import com.msp31.storage1c.domain.entity.account.User;
import com.msp31.storage1c.domain.entity.account.model.UserModel;
import com.msp31.storage1c.domain.mapper.UserMapper;
import com.msp31.storage1c.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    UserMapper userMapper;
    UserRepository userRepository;

    @Override
    public UserInfo registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new UsernameInUseException();
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailInUseException();

        UserModel model = userMapper.createModelFrom(request);
        User user = User.createFromModel(model);
        user = userRepository.save(user);
        return userMapper.createUserInfoFrom(user);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public UserInfo getCurrentUserInfo() {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty())
            throw new UserNotFoundException();

        return userMapper.createUserInfoFrom(user.get());
    }

    @Override
    public PublicUserInfo getPublicUserInfo(long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new UserNotFoundException();

        return userMapper.createPublicUserInfoFrom(user.get());
    }

    @Override
    public long getUserIdByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UserNotFoundException();

        return user.get().getId();
    }
}
