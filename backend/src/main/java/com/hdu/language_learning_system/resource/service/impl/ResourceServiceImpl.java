package com.hdu.language_learning_system.resource.service.impl;

import com.hdu.language_learning_system.resource.dto.*;
import com.hdu.language_learning_system.resource.entity.ResourceCategory;
import com.hdu.language_learning_system.resource.entity.TeachingResource;
import com.hdu.language_learning_system.resource.repository.ResourceCategoryRepository;
import com.hdu.language_learning_system.resource.repository.TeachingResourceRepository;
import com.hdu.language_learning_system.resource.service.ResourceService;
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.repository.UserRepository;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceCategoryRepository categoryRepository;
    @Resource
    private TeachingResourceRepository teachingResourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createCategory(ResourceCategoryCreateDTO dto) {
        ResourceCategory category = new ResourceCategory();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        categoryRepository.save(category);
    }

    //查看资源类目
    @Override
    public List<ResourceCategoryDTO> getAllCategories() {
        List<ResourceCategory> categories = categoryRepository.findAll();
        return categories.stream().map(category -> {
            ResourceCategoryDTO dto = new ResourceCategoryDTO();
            dto.setCategoryId(category.getCategoryId());
            dto.setCategoryName(category.getCategoryName());
            dto.setDescription(category.getDescription());
            return dto;
        }).toList();
    }
    //修改资源类目
    @Override
    public void updateCategory(Integer categoryId, ResourceCategoryCreateDTO dto) {
        ResourceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("资源类目不存在"));

        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());

        categoryRepository.save(category);
    }

    //删除资源类目
    @Override
    public void deleteCategory(Integer categoryId) {
        ResourceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("资源类目不存在"));

        // 检查是否存在关联的教学资源
        long count = teachingResourceRepository.countByCategory_CategoryId(categoryId);
        if (count > 0) {
            throw new RuntimeException("该资源类目下已有教学资源，无法删除");
        }

        categoryRepository.delete(category);
    }

    //上传资源
    @Override
    public void uploadTeachingResource(TeachingResourceCreateDTO dto) {
        ResourceCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("资源类目不存在"));
        User uploader = userRepository.findById(dto.getUploaderId())
                .orElseThrow(() -> new RuntimeException("上传者不存在"));

        TeachingResource resource = new TeachingResource();
        resource.setCategory(category);
        resource.setResourceName(dto.getResourceName());
        resource.setResourceType(dto.getResourceType());
        resource.setUploader(uploader);
        resource.setUploadTime(new Timestamp(System.currentTimeMillis()));
        resource.setAuditStatus("待审核");
        resource.setDownloadCount(0);
        resource.setResourceContent(dto.getResourceContent());

        teachingResourceRepository.save(resource);
    }

    //查看待审核资源列表
    @Override
    public List<TeachingResourceListDTO> getPendingAuditResources() {
        List<TeachingResource> list = teachingResourceRepository.findByAuditStatus("待审核");

        return list.stream().map(resource -> {
            TeachingResourceListDTO dto = new TeachingResourceListDTO();
            dto.setResourceId(resource.getResourceId());
            dto.setResourceName(resource.getResourceName());
            dto.setResourceType(resource.getResourceType());
            dto.setUploadTime(resource.getUploadTime());
            dto.setAuditStatus(resource.getAuditStatus());

            if (resource.getCategory() != null) {
                dto.setCategoryName(resource.getCategory().getCategoryName());
            }
            if (resource.getUploader() != null) {
                dto.setUploaderName(resource.getUploader().getUsername());
            }

            return dto;
        }).toList();
    }

    //审核资源
    @Override
    public void auditTeachingResource(AuditResourceDTO dto) {
        TeachingResource resource = teachingResourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new RuntimeException("资源不存在"));

        if (!"待审核".equals(resource.getAuditStatus())) {
            throw new RuntimeException("该资源已审核过，无法重复审核");
        }

        if (dto.isApproved()) {
            resource.setAuditStatus("已发布");
        } else {
            resource.setAuditStatus("驳回");
        }

        teachingResourceRepository.save(resource);
    }

    //查看资源列表
    @Override
    public List<TeachingResourceListDTO> getPublishedResources(Integer categoryId) {
        List<TeachingResource> list;

        if (categoryId == null) {
            list = teachingResourceRepository.findByAuditStatus("已发布");
        } else {
            list = teachingResourceRepository.findByAuditStatusAndCategory_CategoryId("已发布", categoryId);
        }

        return list.stream().map(resource -> {
            TeachingResourceListDTO dto = new TeachingResourceListDTO();
            dto.setResourceId(resource.getResourceId());
            dto.setResourceName(resource.getResourceName());
            dto.setResourceType(resource.getResourceType());
            dto.setAuditStatus(resource.getAuditStatus());
            dto.setUploadTime(resource.getUploadTime());
            dto.setDownloadCount(resource.getDownloadCount());

            if (resource.getUploader() != null) {
                dto.setUploaderName(resource.getUploader().getUsername());
            }
            if (resource.getCategory() != null) {
                dto.setCategoryName(resource.getCategory().getCategoryName());
            }

            return dto;
        }).toList();
    }

    //下载资源
    @Override
    @Transactional
    public String downloadTeachingResource(Integer resourceId) {
        TeachingResource resource = teachingResourceRepository
                .findByResourceIdAndAuditStatus(resourceId, "已发布")
                .orElseThrow(() -> new RuntimeException("资源不存在或未发布"));

        // 模拟下载行为（这里只是返回资源内容或地址）
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        teachingResourceRepository.save(resource);

        return resource.getResourceContent(); // 假设是资源的 URL 或内容链接
    }
}