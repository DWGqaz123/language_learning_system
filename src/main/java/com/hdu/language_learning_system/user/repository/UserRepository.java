package com.hdu.language_learning_system.user.repository;

import com.hdu.language_learning_system.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // 可添加额外查询方法，例如通过手机号查找用户
    User findByPhoneNumber(String phoneNumber);
}
