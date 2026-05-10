package com.github.b3kt.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationExceptionTest {

    @Test
    void constructorWithMessage_shouldSetMessage() {
        AuthenticationException ex = new AuthenticationException("test error");
        assertEquals("test error", ex.getMessage());
    }

    @Test
    void constructorWithMessageAndCause_shouldSetBoth() {
        Throwable cause = new RuntimeException("root cause");
        AuthenticationException ex = new AuthenticationException("test error", cause);
        assertEquals("test error", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
