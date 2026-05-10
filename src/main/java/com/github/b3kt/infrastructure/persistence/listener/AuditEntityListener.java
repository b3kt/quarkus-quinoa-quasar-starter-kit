package com.github.b3kt.infrastructure.persistence.listener;

import com.github.b3kt.application.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuditEntityListener {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<Integer, String> BEFORE_STATES = new ConcurrentHashMap<>();

    @PostLoad
    public void postLoad(Object entity) {
        BEFORE_STATES.put(System.identityHashCode(entity), toJson(entity));
    }

    @PrePersist
    public void prePersist(Object entity) {
        audit(entity, "CREATE", null, toJson(entity));
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        String beforeJson = BEFORE_STATES.remove(System.identityHashCode(entity));
        audit(entity, "UPDATE", beforeJson, toJson(entity));
    }

    @PreRemove
    public void preRemove(Object entity) {
        String beforeJson = BEFORE_STATES.remove(System.identityHashCode(entity));
        audit(entity, "DELETE", beforeJson, null);
    }

    private void audit(Object entity, String action, String beforeJson, String afterJson) {
        try {
            AuditService auditService = CDI.current().select(AuditService.class).get();
            String entityName = entity.getClass().getSimpleName();
            String entityId = extractId(entity);
            auditService.logChange(entityName, entityId, action, beforeJson, afterJson, null);
        } catch (Exception e) {
            // Silently skip audit logging if service is unavailable
        }
    }

    private String toJson(Object entity) {
        try {
            return MAPPER.writer().writeValueAsString(MAPPER.convertValue(entity, Map.class));
        } catch (Exception e) {
            return "{}";
        }
    }

    private String extractId(Object entity) {
        try {
            var idField = entity.getClass().getMethod("getId");
            Object id = idField.invoke(entity);
            return id != null ? id.toString() : "?";
        } catch (Exception e) {
            return "?";
        }
    }
}
