package com.github.b3kt.infrastructure.persistence.listener;

import com.github.b3kt.infrastructure.persistence.entity.BaseEntity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

@ApplicationScoped
public class AuditListener {

    @Inject
    SecurityIdentity securityIdentity;

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            LocalDateTime now = LocalDateTime.now();
            baseEntity.setCreatedAt(now);
            baseEntity.setUpdatedAt(now);

            if (baseEntity.getVersion() == null) {
                baseEntity.setVersion(0);
            }

            String username = getUsername();
            baseEntity.setCreatedBy(username);
            baseEntity.setUpdatedBy(username);
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) entity;
            baseEntity.setUpdatedAt(LocalDateTime.now());
            baseEntity.setUpdatedBy(getUsername());
        }
    }

    private String getUsername() {
        if (securityIdentity != null && !securityIdentity.isAnonymous()) {
            return securityIdentity.getPrincipal().getName();
        }
        return "system";
    }
}
