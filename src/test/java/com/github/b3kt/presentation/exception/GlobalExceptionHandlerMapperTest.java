package com.github.b3kt.presentation.exception;

import com.github.b3kt.application.dto.ApiResponse;
import com.github.b3kt.domain.exception.AuthenticationException;
import com.github.b3kt.domain.exception.InvalidResetTokenException;
import com.github.b3kt.domain.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class GlobalExceptionHandlerMapperTest {

    @Test
    void authenticationExceptionMapper_shouldReturn401() {
        var mapper = new GlobalExceptionHandler.AuthenticationExceptionMapper();
        AuthenticationException ex = new AuthenticationException("bad credentials");
        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        ApiResponse<?> entity = (ApiResponse<?>) response.getEntity();
        assertFalse(entity.isSuccess());
        assertEquals("bad credentials", entity.getError());
    }

    @Test
    void userNotFoundExceptionMapper_shouldReturn404() {
        var mapper = new GlobalExceptionHandler.UserNotFoundExceptionMapper();
        UserNotFoundException ex = new UserNotFoundException("user not found");
        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        ApiResponse<?> entity = (ApiResponse<?>) response.getEntity();
        assertFalse(entity.isSuccess());
        assertEquals("user not found", entity.getError());
    }

    @Test
    void constraintViolationExceptionMapper_shouldReturn400() {
        var mapper = new GlobalExceptionHandler.ConstraintViolationExceptionMapper();
        ConstraintViolationException ex = new ConstraintViolationException("validation failed", Set.of());
        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ApiResponse<?> entity = (ApiResponse<?>) response.getEntity();
        assertFalse(entity.isSuccess());
    }

    @Test
    void invalidResetTokenExceptionMapper_shouldReturn400() {
        var mapper = new GlobalExceptionHandler.InvalidResetTokenExceptionMapper();
        InvalidResetTokenException ex = new InvalidResetTokenException("token expired");
        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ApiResponse<?> entity = (ApiResponse<?>) response.getEntity();
        assertFalse(entity.isSuccess());
        assertEquals("token expired", entity.getError());
    }

    @Test
    void illegalArgumentExceptionMapper_shouldReturn400() {
        var mapper = new GlobalExceptionHandler.IllegalArgumentExceptionMapper();
        IllegalArgumentException ex = new IllegalArgumentException("bad argument");
        Response response = mapper.toResponse(ex);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        ApiResponse<?> entity = (ApiResponse<?>) response.getEntity();
        assertFalse(entity.isSuccess());
        assertEquals("bad argument", entity.getError());
    }
}
