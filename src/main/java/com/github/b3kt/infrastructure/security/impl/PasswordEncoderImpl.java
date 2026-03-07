package com.github.b3kt.infrastructure.security.impl;

import com.github.b3kt.infrastructure.security.PasswordEncoder;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(String rawPassword) {
        return BcryptUtil.bcryptHash(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            return BcryptUtil.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}

