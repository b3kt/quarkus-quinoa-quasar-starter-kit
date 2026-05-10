package com.github.b3kt.application.mapper;

import com.github.b3kt.application.dto.AuditLogResponse;
import com.github.b3kt.infrastructure.persistence.entity.AuditLogEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AuditLogMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static AuditLogResponse toResponse(AuditLogEntity entity) {
        if (entity == null) return null;
        return new AuditLogResponse(
            entity.getId(),
            entity.getEntityName(),
            entity.getEntityId(),
            entity.getAction(),
            parseJson(entity.getBeforeValues()),
            parseJson(entity.getAfterValues()),
            entity.getChangedBy(),
            entity.getChangedAt()
        );
    }

    private static Object parseJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return MAPPER.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return json;
        }
    }
}
