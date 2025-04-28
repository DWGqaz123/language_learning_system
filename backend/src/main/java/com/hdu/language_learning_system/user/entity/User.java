package com.hdu.language_learning_system.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
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


    @LastModifiedDate
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Role实体外键映射
    @ManyToOne(fetch = FetchType.EAGER)
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


    // 暂存用户修改请求
    @Column(name = "pending_update", columnDefinition = "TEXT")
    private String pendingUpdateJson;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return userId != null && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId);
    }
}
