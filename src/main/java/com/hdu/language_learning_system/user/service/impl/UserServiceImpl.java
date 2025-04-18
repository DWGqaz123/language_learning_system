package com.hdu.language_learning_system.user.service.impl;
//初始化
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.user.service.UserService;
//DTO
import com.hdu.language_learning_system.user.dto.UserDTO;
import com.hdu.language_learning_system.user.dto.EmployeeImportDTO;
import com.hdu.language_learning_system.user.dto.StudentRegisterDTO;
import com.hdu.language_learning_system.user.dto.UpdateUserRoleDTO;
import com.hdu.language_learning_system.user.dto.UserActivationDTO;
import com.hdu.language_learning_system.user.dto.UpdateUserInfoDTO;
import com.hdu.language_learning_system.user.dto.UserUpdateAuditDTO;
//实体
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.entity.Role;
//其他
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 私有转换方法
    private UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setDescription(user.getDescription());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getRoleId());
            dto.setRoleName(user.getRole().getRoleName());
        }

        return dto;
    }

    // 通过手机号查找用户
    @Override
    public UserDTO findByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        return convertToDTO(user);
    }

    // 查询所有用户
    @Override
    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 员工信息导入（管理员导入教师和助教信息）
    @Override
    public void importEmployee(EmployeeImportDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setAccountStatus(dto.getAccountStatus());
        user.setDescription(dto.getDescription());

        // 设置角色
        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        user.setRole(role);
        userRepository.save(user);
    }

    // 学员信息导入
    @Override
    public void registerStudent(StudentRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        user.setAccountStatus(false); // 默认关闭状态
        user.setDescription(dto.getDescription());

        // 设置默认角色为学员（role_id = 1）
        Role role = new Role();
        role.setRoleId(1);
        user.setRole(role);

        userRepository.save(user);
    }

    // 用户权限分配
    @Override
    public void updateUserRole(UpdateUserRoleDTO dto) {
        // 获取操作人
        User operator = userRepository.findById(dto.getOperatorUserId())
                .orElseThrow(() -> new RuntimeException("操作用户不存在"));

        // 判断操作人角色是否为“管理员”
        if (operator.getRole().getRoleId() != 4) { // 假设角色ID 4 为管理员
            throw new RuntimeException("只有管理员可以修改用户角色");
        }

        // 修改目标用户角色
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("目标用户不存在"));

        Role newRole = new Role();
        newRole.setRoleId(dto.getRoleId());
        user.setRole(newRole);

        userRepository.save(user);
    }

    // 账号激活
    @Override
    public void activateUser(UserActivationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setPassword(dto.getNewPassword());
        user.setAccountStatus(true); // 设置为已激活
        userRepository.save(user);
    }

    //用户信息修改
    @Override
    public void updateUserInfo(UpdateUserInfoDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDescription(dto.getDescription());
        userRepository.save(user);
    }

    //用户提交修改请求

    @Override
    public void submitUserUpdateRequest(UserUpdateAuditDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String pendingJson = objectMapper.writeValueAsString(dto);
            user.setPendingUpdateJson(pendingJson);
            userRepository.save(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("数据序列化失败", e);
        }
    }

    //管理员审核用户信息修改请求
    @Override
    public void reviewUserUpdateRequest(UserUpdateAuditDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!Boolean.TRUE.equals(dto.getApproved())) {
            user.setPendingUpdateJson(null); // 审核不通过则清空
            userRepository.save(user);
            return;
        }

        // 审核通过：应用更改
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDescription(dto.getDescription());
        user.setPendingUpdateJson(null);
        userRepository.save(user);
    }

}
