package com.hdu.language_learning_system.resource.service;

import com.hdu.language_learning_system.resource.dto.*;

import java.util.List;

public interface ResourceService {
    void createCategory(ResourceCategoryCreateDTO dto);
    List<ResourceCategoryDTO> getAllCategories();

    void updateCategory(Integer categoryId, ResourceCategoryCreateDTO dto);

    void deleteCategory(Integer categoryId);

    void uploadTeachingResource(TeachingResourceCreateDTO dto);

    List<TeachingResourceListDTO> getPendingAuditResources();

    void auditTeachingResource(AuditResourceDTO dto);

    List<TeachingResourceListDTO> getPublishedResources(Integer categoryId);

    String downloadTeachingResource(Integer resourceId);
}