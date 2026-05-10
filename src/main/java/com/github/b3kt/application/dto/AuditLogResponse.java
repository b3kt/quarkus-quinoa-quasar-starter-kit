package com.github.b3kt.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Date;

@Schema(description = "Audit log entry with before/after values")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    @Schema(description = "Audit log ID", example = "1")
    private Long id;

    @Schema(description = "Entity name (table)", example = "UserEntity")
    private String entityName;

    @Schema(description = "Entity ID", example = "1")
    private String entityId;

    @Schema(description = "Action performed", example = "UPDATE")
    private String action;

    @Schema(description = "Values before the change (JSON)")
    private Object beforeValues;

    @Schema(description = "Values after the change (JSON)")
    private Object afterValues;

    @Schema(description = "User who made the change", example = "admin")
    private String changedBy;

    @Schema(description = "When the change occurred")
    private Date changedAt;

}
