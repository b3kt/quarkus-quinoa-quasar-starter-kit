package com.github.b3kt.application.service.impl;

import com.github.b3kt.application.dto.AuditLogResponse;
import com.github.b3kt.application.mapper.AuditLogMapper;
import com.github.b3kt.application.service.AuditService;
import com.github.b3kt.infrastructure.persistence.entity.AuditLogEntity;
import com.github.b3kt.infrastructure.persistence.repository.AuditLogRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AuditServiceImpl implements AuditService {

    @Inject
    AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void logChange(String entityName, String entityId, String action,
                          String beforeValues, String afterValues, String changedBy) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setEntityName(entityName);
        entity.setEntityId(entityId);
        entity.setAction(action);
        entity.setBeforeValues(beforeValues);
        entity.setAfterValues(afterValues);
        entity.setChangedBy(changedBy);
        entity.setChangedAt(new Date());
        auditLogRepository.persist(entity);
    }

    @Override
    public List<AuditLogResponse> findAll() {
        return auditLogRepository.findAllOrdered().stream()
                .map(AuditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponse> findByEntityName(String entityName) {
        return auditLogRepository.findByEntityName(entityName).stream()
                .map(AuditLogMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditLogResponse> findByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .map(AuditLogMapper::toResponse)
                .collect(Collectors.toList());
    }
}
