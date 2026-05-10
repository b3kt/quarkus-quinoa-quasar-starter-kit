package com.github.b3kt.infrastructure.persistence.repository;

import com.github.b3kt.infrastructure.persistence.entity.AuditLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AuditLogRepository implements PanacheRepository<AuditLogEntity> {

    public List<AuditLogEntity> findAllOrdered() {
        return find("ORDER BY changedAt DESC").list();
    }

    public List<AuditLogEntity> findByEntityName(String entityName) {
        return find("entityName", entityName).list();
    }

    public List<AuditLogEntity> findByAction(String action) {
        return find("action", action).list();
    }
}
