package com.github.b3kt.application.mapper;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.domain.model.User;

/**
 * Mapper for converting between domain entities and DTOs.
 */
public class UserMapper {

    public static UserInfo toUserInfo(User user) {
        if (user == null) {
            return null;
        }
        return new UserInfo(
            user.getUsername(),
            user.getEmail(),
            user.getRoles()
        );
    }
}

