package com.hdu.language_learning_system.resource.repository;

import com.hdu.language_learning_system.resource.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Integer> {
}