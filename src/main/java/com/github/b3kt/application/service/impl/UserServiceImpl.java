package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.application.mapper.UserMapper;
import com.github.b3kt.application.service.UserService;
import com.github.b3kt.domain.exception.UserNotFoundException;
import com.github.b3kt.domain.model.User;
import com.github.b3kt.infrastructure.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    @Override
    public List<UserInfo> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserInfo)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::toUserInfo)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public UserInfo updateRoles(String username, Set<String> roles) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        user.setRoles(new HashSet<>(roles));
        User savedUser = userRepository.save(user);
        return UserMapper.toUserInfo(savedUser);
    }

    @Override
    @Transactional
    public UserInfo activateUser(String username, boolean active) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        user.setActive(active);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserInfo(savedUser);
    }
}
