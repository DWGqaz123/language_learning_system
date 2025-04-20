## 接口记录
### 用户管理模块
1. GET /api/users — 获取所有用户列表
2. GET /api/users/phone — 通过手机号查询用户
3. POST /api/users/import-employee — 员工信息导入（教师/助教）
4. POST /api/users/register-student — 学员信息录入
5. PUT /api/users/{userId}/update-role — 用户角色变更（管理员操作）
6. POST /api/users/activate-account — 账号首次激活与密码设置
7. POST /api/users/update-request — 提交用户信息修改请求
8. PUT /api/users/review-update — 管理员审核并更新用户信息

### 课程管理模块
1. POST /api/courses/create — 创建课程
2. POST /api/courses/add-students-to-class — 添加新学员到班级课程
3. POST /api/schedules/create — 创建课程表
4. POST /api/courses/student-records/generate — 生成学员课程记录
5. POST /api/courses/student/leave-request — 学员请假申请
6. POST /api/courses/review-leave —请假申请审核
7. GET /api/courses/attendance-record — 查询课程考勤统计
8. POST /api/courses/update-attendance-status — 更新考勤状态
9. POST /api/courses/update-performance-eval — 更新课堂表现评价
10. GET /api/courses/{courseId}/schedules — 查询课程排课列表    
11. GET /api/courses/my-schedules — 分角色查看课表
12. GET /api/courses/schedule/{scheduleId}/students-status — 查看某次课学员状态
13. GET /api/courses/student/attendance-performance — 学员查看个人考勤与表现
14. GET /api/courses/my-courses — 根据用户查询课程列表

### 通知模块
1. POST /api/notifications/send — 单条通知发送
2. POST /api/notifications/send-batch — 批量发送给课程学员和教师
3. POST /api/notifications/query — 获取接收人的通知列表（支持分页、可选类型过滤）
4. POST /api/notifications/mark-as-read — 标记通知为已读

### 课后管理模块
1. GET /api/task-assignments/student/{studentId}/submissions — 查看某个学员的任务提交记录
2. GET /api/task-assignments/task/{taskId}/submissions — 查看某个任务的所有提交记录
3. POST /api/task-assignments/grade — 助教批改打分任务
4. POST /api/tasks/publish — 课后任务发布
5. GET /api/tasks/student/{studentId}/list — 学员查看课后任务清单
6. POST /api/tasks/submit — 学员提交课后任务
7. GET /api/tasks/{taskId}/details — 查看任务详情
8. GET /api/tasks/published — 查看助教自己发布的任务
9. GET /api/task-assignments/student/{studentId}/stats — 获取学员任务统计数据
