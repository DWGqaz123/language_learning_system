package com.hdu.language_learning_system.resource.dto;

import lombok.Data;

@Data
public class TeachingResourceCreateDTO {
    private Integer categoryId;
    private String resourceName;
    private String resourceType;
    private Integer uploaderId;
    private String resourceContent;
}