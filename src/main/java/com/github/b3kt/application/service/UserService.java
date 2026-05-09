package com.github.b3kt.application.service;

import com.github.b3kt.application.dto.UserInfo;
import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserInfo> findAll();

    UserInfo findByUsername(String username);

    UserInfo updateRoles(String username, Set<String> roles);

    UserInfo activateUser(String username, boolean active);
}
