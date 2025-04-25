package com.hdu.language_learning_system.user.controller;

import com.hdu.language_learning_system.common.ApiResponse;
import com.hdu.language_learning_system.user.dto.*;
import com.hdu.language_learning_system.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired // 自动注入 UserService
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 1. 查询所有用户
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // 2. 通过手机号查询用户
    @GetMapping("/phone")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByPhoneNumber(@RequestParam String phoneNumber) {
        UserDTO user = userService.findByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // 3. 导入员工信息
    @PostMapping("/import-employee")
    public ResponseEntity<ApiResponse<Void>> importEmployee(@RequestBody EmployeeImportDTO dto) {
        userService.importEmployee(dto);
        return ResponseEntity.ok(ApiResponse.success("员工导入成功", null));
    }

    // 4. 录入学员信息
    @PostMapping("/register-student")
    public ApiResponse<Map<String, Integer>> registerStudent(@RequestBody StudentRegisterDTO dto) {
        return userService.registerStudent(dto);
    }

    // 5. 用户权限分配
    @PutMapping("/update-role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(@RequestBody UpdateUserRoleDTO dto) {
        userService.updateUserRole(dto);
        return ResponseEntity.ok(ApiResponse.success("用户权限更新成功", null));
    }
    // 6. 用户账号激活
    @PostMapping("/activate-account")
    public ResponseEntity<ApiResponse<Void>> activateUser(@RequestBody UserActivationDTO dto) {
        userService.activateUser(dto);
        return ResponseEntity.ok(ApiResponse.success("账号激活成功", null));
    }
    // 7. 用户信息修改
    @PutMapping("/update-info")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(@RequestBody UpdateUserInfoDTO dto) {
        userService.updateUserInfo(dto);
        return ResponseEntity.ok(ApiResponse.success("用户信息更新成功", null));
    }
    // 8. 用户信息修改请求
    @PostMapping("/update-request")
    public ResponseEntity<ApiResponse<Void>> submitUpdateRequest(@RequestBody UserUpdateAuditDTO dto) {
        userService.submitUserUpdateRequest(dto);
        return ResponseEntity.ok(ApiResponse.success("修改申请提交成功", null));
    }

    // 9. 管理员审核用户信息修改
    @PutMapping("/review-update")
    public ResponseEntity<ApiResponse<Void>> reviewUpdateRequest(@RequestBody UserUpdateAuditDTO dto) {
        userService.reviewUserUpdateRequest(dto);
        return ResponseEntity.ok(ApiResponse.success("审核处理完成", null));
    }
    //用户登陆
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginDTO dto) {
        try {
            LoginResponseDTO userInfo = userService.login(dto);
            return ApiResponse.success(userInfo);
        } catch (Exception e) {
            return ApiResponse.error("登录失败：" + e.getMessage());
        }
    }

    //查找学员列表
    @GetMapping("/all-students")
    public ResponseEntity<ApiResponse<List<StudentSimpleInfoDTO>>> getAllStudents() {
        List<StudentSimpleInfoDTO> students = userService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success(students));
    }

    // 根据用户ID查询用户信息
    @GetMapping("/get-by-id")
    public ApiResponse<UserDTO> getUserById(@RequestParam Integer userId) {
        UserDTO dto = userService.getUserById(userId);
        return ApiResponse.success(dto);
    }

    //测试
    @GetMapping("/test")
    public String test() {
        return "proxy success";
    }

    //查询待审核用户信息修改列表
    @GetMapping("/pending-update-requests")
    public ApiResponse<List<PendingUpdateUserDTO>> getPendingUpdateRequests() {
        List<PendingUpdateUserDTO> list = userService.getPendingUpdateRequests();
        return ApiResponse.success(list);
    }

}
