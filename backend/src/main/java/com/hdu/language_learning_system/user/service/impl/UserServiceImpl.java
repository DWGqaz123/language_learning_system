package com.hdu.language_learning_system.user.service.impl;
//初始化
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hdu.language_learning_system.user.repository.UserRepository;
import com.hdu.language_learning_system.user.service.UserService;
//DTO
//实体
import com.hdu.language_learning_system.user.entity.User;
import com.hdu.language_learning_system.user.entity.Role;
//其他
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        user.setPassword("123456");
        user.setAccountStatus(false);
        user.setDescription(dto.getDescription());

        // 设置角色
        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        user.setRole(role);
        userRepository.save(user);
    }

    // 学员信息导入
    @Override
    public ApiResponse<Map<String, Integer>> registerStudent(StudentRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword("123456");
        user.setAccountStatus(false);
        user.setDescription(dto.getDescription());
        user.setLessonHours(dto.getLessonHours() != null ? dto.getLessonHours() : 0);

        Role role = new Role();
        role.setRoleId(1); // 学员
        user.setRole(role);

        userRepository.save(user);

        Map<String, Integer> data = new HashMap<>();
        data.put("userId", user.getUserId());

        return ApiResponse.success("学员录入成功", data);
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
        user.setPassword(dto.getPassword());
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
        user.setPassword(dto.getPassword());
        user.setPendingUpdateJson(null);
        userRepository.save(user);
    }

    //用户登陆
    @Override
    public LoginResponseDTO login(LoginDTO dto) {
        User user = userRepository.findByPhoneNumber(dto.getPhoneNumber());
        if (user == null || !user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("登录失败：手机号或密码错误");
        }

        // ✅ 显式更新 updatedAt 字段
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRoleName(user.getRole().getRoleName());
        response.setAccountStatus(user.getAccountStatus());
        return response;
    }

    //查询待审核用户信息修改列表
    @Override
    public List<PendingUpdateUserDTO> getPendingUpdateRequests() {
        List<User> users = userRepository.findByPendingUpdateJsonIsNotNull();

        return users.stream().map(user -> {
            PendingUpdateUserDTO dto = new PendingUpdateUserDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setRoleName(user.getRole().getRoleName());
            dto.setPendingUpdateJson(user.getPendingUpdateJson());
            return dto;
        }).collect(Collectors.toList());
    }

    //查找学员列表
    @Override
    public List<StudentSimpleInfoDTO> getAllStudents() {
        List<User> students = userRepository.findAllStudents();

        return students.stream().map(user -> {
            StudentSimpleInfoDTO dto = new StudentSimpleInfoDTO();
            dto.setUserId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setPhoneNumber(user.getPhoneNumber());
            return dto;
        }).toList();
    }
    //根据userId查询用户
    @Override
    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoleName(user.getRole() != null ? user.getRole().getRoleName() : null);
        dto.setAccountStatus(user.getAccountStatus());
        dto.setDescription(user.getDescription());
        dto.setLessonHours(user.getLessonHours());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
