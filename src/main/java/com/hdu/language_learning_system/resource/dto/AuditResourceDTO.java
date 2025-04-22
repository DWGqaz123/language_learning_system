package com.hdu.language_learning_system.resource.dto;

import lombok.Data;

@Data
public class AuditResourceDTO {
    private Integer resourceId;
    private boolean approved; // true: 通过审核，false: 驳回（可扩展）
}