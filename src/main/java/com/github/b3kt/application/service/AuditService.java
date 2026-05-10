package com.github.b3kt.application.service;

import com.github.b3kt.application.dto.AuditLogResponse;

import java.util.List;

public interface AuditService {

    void logChange(String entityName, String entityId, String action,
                   String beforeValues, String afterValues, String changedBy);

    List<AuditLogResponse> findAll();

    List<AuditLogResponse> findByEntityName(String entityName);

    List<AuditLogResponse> findByAction(String action);
}
