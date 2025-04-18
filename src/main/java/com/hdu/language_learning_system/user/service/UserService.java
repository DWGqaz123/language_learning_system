package com.hdu.language_learning_system.user.service;

import com.hdu.language_learning_system.user.dto.StudentRegisterDTO;
import com.hdu.language_learning_system.user.dto.UserDTO;
import com.hdu.language_learning_system.user.dto.EmployeeImportDTO;
import com.hdu.language_learning_system.user.dto.UpdateUserRoleDTO;
import com.hdu.language_learning_system.user.dto.UserActivationDTO;
import com.hdu.language_learning_system.user.dto.UpdateUserInfoDTO;
import com.hdu.language_learning_system.user.dto.UserUpdateAuditDTO;
import java.util.List;

public interface UserService {

    // 通过手机号查找用户
    UserDTO findByPhoneNumber(String phoneNumber);

    // 查询所有用户
    List<UserDTO> findAllUsers();

    // 员工信息导入（管理员导入教师和助教信息）
    void importEmployee(EmployeeImportDTO employeeDTO);

    // 学员录入（助教把新学员录入系统）
    void registerStudent(StudentRegisterDTO dto);

    // 用户权限更改（角色更改）后续可以继续升级完成细粒度的权限分配的功能
    // 接口 UserService 中修改：
    void updateUserRole(UpdateUserRoleDTO dto);

    // 用户账号激活
    void activateUser(UserActivationDTO dto);

    // 用户信息修改
    void updateUserInfo(UpdateUserInfoDTO dto);

    // 用户提交修改申请
    void submitUserUpdateRequest(UserUpdateAuditDTO dto);

    // 管理员审核申请
    void reviewUserUpdateRequest(UserUpdateAuditDTO dto);

}
