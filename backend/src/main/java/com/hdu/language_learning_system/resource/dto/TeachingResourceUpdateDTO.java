package com.hdu.language_learning_system.resource.dto;

import lombok.Data;

@Data
public class TeachingResourceUpdateDTO {
    private Integer resourceId;
    private String resourceName;
    private Integer categoryId;
    private String resourceType;
    private String resourceContent;
}