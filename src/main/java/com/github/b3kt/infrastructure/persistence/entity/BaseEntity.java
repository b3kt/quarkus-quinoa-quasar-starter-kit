package com.github.b3kt.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import java.util.Date;

@MappedSuperclass
@Data
public abstract class BaseEntity {

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_at", columnDefinition = "timestamp DEFAULT current_timestamp")
    private Date createdAt;

    @Column(name = "updated_at", columnDefinition = "timestamp DEFAULT current_timestamp")
    private Date updatedAt;

    @Column(name = "mark_for_delete")
    private boolean markForDelete = false;
    private long version = 0L;
}
