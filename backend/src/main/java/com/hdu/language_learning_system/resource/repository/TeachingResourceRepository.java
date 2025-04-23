package com.hdu.language_learning_system.resource.repository;

import com.hdu.language_learning_system.resource.entity.TeachingResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeachingResourceRepository extends JpaRepository<TeachingResource, Integer> {

    // 用于删除资源类目前判断是否有关联资源
    long countByCategory_CategoryId(Integer categoryId);

    List<TeachingResource> findByAuditStatus(String auditStatus);

    List<TeachingResource> findByAuditStatusAndCategory_CategoryId(String auditStatus, Integer categoryId);

    Optional<TeachingResource> findByResourceIdAndAuditStatus(Integer resourceId, String auditStatus);
}