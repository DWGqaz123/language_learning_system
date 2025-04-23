package com.hdu.language_learning_system.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(nullable = false)
    private String roleName;

    @Column
    private String permissions;

    // 可选：一个角色可能对应多个用户
    @OneToMany(mappedBy = "role")
    private Set<User> users;

    // 构造方法
    public Role() {}

    public Role(String roleName, String permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    // Getter 和 Setter
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
