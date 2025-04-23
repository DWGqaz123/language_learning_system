package com.hdu.language_learning_system.resource.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TeachingResourceListDTO {
    private Integer resourceId;
    private String resourceName;
    private String resourceType;
    private String categoryName;
    private String uploaderName;
    private Timestamp uploadTime;
    private String auditStatus;
    private Integer downloadCount;
}