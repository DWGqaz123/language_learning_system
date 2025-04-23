package com.hdu.language_learning_system.resource.entity;

import com.hdu.language_learning_system.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "teaching_resources")
@Data
public class TeachingResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Integer resourceId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private ResourceCategory category;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "resource_type")
    private String resourceType; // 视频 / 文档 / 音频

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @Column(name = "upload_time")
    private Timestamp uploadTime;

    @Column(name = "audit_status")
    private String auditStatus; // 待审核 or 已发布

    @Column(name = "download_count")
    private Integer downloadCount;

    @Column(name = "resource_content", columnDefinition = "TEXT")
    private String resourceContent; // 可为链接地址或路径
}