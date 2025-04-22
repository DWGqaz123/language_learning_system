package com.hdu.language_learning_system.resource.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resource_categorys") // 表名为 resource_categorys
@Data
public class ResourceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;
}