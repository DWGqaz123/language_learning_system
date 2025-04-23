package com.hdu.language_learning_system.resource.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.resource.dto.*;
import com.hdu.language_learning_system.resource.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-categories")
public class ResourceCategoryController {

    @Autowired
    private ResourceService resourceService;

    // 新增资源类目
    @PostMapping
    public ApiResponse<String> createCategory(@RequestBody ResourceCategoryCreateDTO dto) {
        try {
            resourceService.createCategory(dto);
            return ApiResponse.success("新增资源类目成功", null);
        } catch (Exception e) {
            return ApiResponse.error("新增失败：" + e.getMessage());
        }
    }

    //查看资源类目
    @GetMapping
    public ApiResponse<List<ResourceCategoryDTO>> getAllCategories() {
        try {
            List<ResourceCategoryDTO> list = resourceService.getAllCategories();
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }

    //修改资源类目
    @PutMapping("/{categoryId}")
    public ApiResponse<String> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody ResourceCategoryCreateDTO dto) {
        try {
            resourceService.updateCategory(categoryId, dto);
            return ApiResponse.success("修改成功");
        } catch (Exception e) {
            return ApiResponse.error("修改失败：" + e.getMessage());
        }
    }

    //删除资源类目
    @DeleteMapping("/{categoryId}")
    public ApiResponse<String> deleteCategory(@PathVariable Integer categoryId) {
        try {
            resourceService.deleteCategory(categoryId);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除失败：" + e.getMessage());
        }
    }

    //上传资源
    @PostMapping("/upload")
    public ApiResponse<String> uploadTeachingResource(@RequestBody TeachingResourceCreateDTO dto) {
        try {
            resourceService.uploadTeachingResource(dto);
            return ApiResponse.success("资源上传成功");
        } catch (Exception e) {
            return ApiResponse.error("资源上传失败：" + e.getMessage());
        }
    }

    // 获取待审核资源列表（管理员）
    @GetMapping("/pending")
    public ApiResponse<List<TeachingResourceListDTO>> getPendingAuditResources() {
        try {
            List<TeachingResourceListDTO> list = resourceService.getPendingAuditResources();
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取待审核资源失败：" + e.getMessage());
        }
    }

    //审核资源
    @PostMapping("/audit")
    public ApiResponse<String> auditTeachingResource(@RequestBody AuditResourceDTO dto) {
        try {
            resourceService.auditTeachingResource(dto);
            return ApiResponse.success("审核成功");
        } catch (Exception e) {
            return ApiResponse.error("审核失败：" + e.getMessage());
        }
    }

    // 查看已发布教学资源（可选类目筛选）
    @GetMapping("/published")
    public ApiResponse<List<TeachingResourceListDTO>> getPublishedResources(
            @RequestParam(required = false) Integer categoryId) {
        try {
            List<TeachingResourceListDTO> list = resourceService.getPublishedResources(categoryId);
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取失败：" + e.getMessage());
        }
    }

    //下载资源
    // 学员下载资源
    @GetMapping("/download/{resourceId}")
    public ApiResponse<String> downloadResource(@PathVariable Integer resourceId) {
        try {
            String content = resourceService.downloadTeachingResource(resourceId);
            return ApiResponse.success("下载成功", content);
        } catch (Exception e) {
            return ApiResponse.error("下载失败：" + e.getMessage());
        }
    }
}