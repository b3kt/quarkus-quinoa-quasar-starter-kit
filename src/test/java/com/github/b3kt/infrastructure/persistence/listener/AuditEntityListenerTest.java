package com.github.b3kt.infrastructure.persistence.listener;

import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

class AuditEntityListenerTest {

    static class TestEntity {
        @Id
        private Long id = 42L;

        public Long getId() {
            return id;
        }

        public String getSomeField() {
            return "value";
        }
    }

    static class EntityWithoutId {
        private String name = "test";
    }

    private final AuditEntityListener listener = new AuditEntityListener();

    @Test
    void postLoad_shouldStoreState() {
        listener.postLoad(new TestEntity());
    }

    @Test
    void prePersist_shouldAuditCreate() {
        listener.prePersist(new TestEntity());
    }

    @Test
    void preUpdate_shouldAuditUpdate() {
        TestEntity entity = new TestEntity();
        listener.postLoad(entity);
        listener.preUpdate(entity);
    }

    @Test
    void preRemove_shouldAuditDelete() {
        TestEntity entity = new TestEntity();
        listener.postLoad(entity);
        listener.preRemove(entity);
    }

    @Test
    void preRemove_withoutPostLoad_shouldHandleNullBeforeState() {
        listener.preRemove(new TestEntity());
    }

    static class BadEntity {
        public String getName() {
            throw new RuntimeException("Jackson serialization error");
        }
    }

    @Test
    void toJson_shouldHandleEntityWithCircularReferenceViaException() {
        listener.prePersist(new EntityWithoutId());
    }

    @Test
    void toJson_shouldReturnEmptyJson_whenSerializationFails() {
        listener.prePersist(new BadEntity());
    }

    @Test
    void extractId_shouldReturnQuestionMark_whenNoGetIdMethod() {
        listener.prePersist(new EntityWithoutId());
    }
}
