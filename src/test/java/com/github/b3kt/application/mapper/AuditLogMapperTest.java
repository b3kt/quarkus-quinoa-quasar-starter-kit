package com.github.b3kt.application.mapper;

import com.github.b3kt.application.dto.AuditLogResponse;
import com.github.b3kt.infrastructure.persistence.entity.AuditLogEntity;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogMapperTest {

    @Test
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertNull(AuditLogMapper.toResponse(null));
    }

    @Test
    void toResponse_shouldMapAllFields() {
        Date now = new Date();
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(1L);
        entity.setEntityName("UserEntity");
        entity.setEntityId("42");
        entity.setAction("UPDATE");
        entity.setBeforeValues(null);
        entity.setAfterValues(null);
        entity.setChangedBy("admin");
        entity.setChangedAt(now);

        AuditLogResponse response = AuditLogMapper.toResponse(entity);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("UserEntity", response.getEntityName());
        assertEquals("42", response.getEntityId());
        assertEquals("UPDATE", response.getAction());
        assertNull(response.getBeforeValues());
        assertNull(response.getAfterValues());
        assertEquals("admin", response.getChangedBy());
        assertEquals(now, response.getChangedAt());
    }

    @Test
    void toResponse_shouldParseValidJson() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(2L);
        entity.setEntityName("UserEntity");
        entity.setEntityId("1");
        entity.setAction("CREATE");
        entity.setBeforeValues("{\"key\":\"value\"}");
        entity.setAfterValues("[1,2,3]");
        entity.setChangedBy("admin");
        entity.setChangedAt(new Date());

        AuditLogResponse response = AuditLogMapper.toResponse(entity);
        assertNotNull(response);
        assertInstanceOf(Map.class, response.getBeforeValues());
        assertEquals("value", ((Map<String, Object>) response.getBeforeValues()).get("key"));
    }

    @Test
    void toResponse_shouldReturnRawJson_whenInvalidJson() {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(3L);
        entity.setEntityName("UserEntity");
        entity.setEntityId("1");
        entity.setAction("CREATE");
        entity.setBeforeValues("{invalid json}");
        entity.setAfterValues(null);
        entity.setChangedBy("admin");
        entity.setChangedAt(new Date());

        AuditLogResponse response = AuditLogMapper.toResponse(entity);
        assertNotNull(response);
        assertEquals("{invalid json}", response.getBeforeValues());
    }
}
