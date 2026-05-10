package com.github.b3kt.application.mapper;

import com.github.b3kt.application.dto.UserInfo;
import com.github.b3kt.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserInfo_shouldReturnNull_whenUserIsNull() {
        assertNull(UserMapper.toUserInfo(null));
    }

    @Test
    void toUserInfo_shouldMapAllFields() {
        Set<String> roles = Set.of("user", "admin");
        User user = new User("testuser", "test@example.com", "hash", roles);
        UserInfo info = UserMapper.toUserInfo(user);
        assertNotNull(info);
        assertEquals("testuser", info.getUsername());
        assertEquals("test@example.com", info.getEmail());
        assertEquals(roles, info.getRoles());
    }
}
