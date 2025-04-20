package com.hdu.language_learning_system.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "username")
    private String username;

    @Column(unique = true)
    private String phoneNumber;

    private String password;

    private Boolean accountStatus;

    private String description;

    @Column(name = "lesson_hours")
    private Integer lessonHours; // 记录学员拥有的1对1课时

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Role实体外键映射
    @ManyToOne(fetch = FetchType.EAGER) //懒加载，提升性能
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonIgnore
    private Role role;
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }



//    public String getUsername() {//debug
//        return username;
//    }


    // 暂存用户修改请求
    @Column(name = "pending_update", columnDefinition = "TEXT")
    private String pendingUpdateJson;


}
