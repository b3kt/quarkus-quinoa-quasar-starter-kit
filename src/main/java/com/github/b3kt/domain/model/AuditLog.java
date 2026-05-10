package com.github.b3kt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private Long id;
    private String entityName;
    private String entityId;
    private String action;
    private String beforeValues;
    private String afterValues;
    private String changedBy;
    private Date changedAt;
}
