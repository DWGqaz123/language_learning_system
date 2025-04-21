## 接口文档(简单版)
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
3. POST /api/courses/student-records/generate — 生成学员课程记录
4. POST /api/courses/student/leave-request — 学员请假申请
5. GET /api/courses/attendance-record — 查询课程考勤统计
6. POST /api/courses/update-attendance-status — 更新考勤状态
7. POST /api/courses/update-performance-eval — 更新课堂表现评价
8. GET /api/courses/{courseId}/schedules — 查询课程排课列表
9. GET /api/courses/my-schedules — 分角色查看课表
10. GET /api/courses/schedule/{scheduleId}/students-status — 查看某次课学员状态
11. GET /api/courses/student/attendance-performance — 学员查看个人考勤与表现
12. GET /api/courses/my-courses — 根据用户查询课程列表
13. POST /api/courses/review-leave — 审核学员请假
14. PUT /api/courses/update — 修改课程信息
15. DELETE /api/courses/{courseId} — 删除课程
16. DELETE /api/courses/remove-student — 移除班级学员
17. GET /api/courses/schedules/{scheduleId} — 查询排课详情
18. GET /api/courses/{courseId}/schedule/{scheduleId}/leave-requests — 查询某课程某课表下的请假申请
19. POST /api/schedules/create — 创建课程表
20. PUT /api/schedules/update — 修改排课
21. DELETE /api/schedules/{scheduleId}/delete — 删除排课

### 通知模块
1. POST /api/notifications/send — 单条通知发送
2. POST /api/notifications/send-batch — 批量发送给课程学员和教师
3. POST /api/notifications/query — 获取接收人的通知列表（支持分页、可选类型过滤）
4. POST /api/notifications/mark-as-read — 标记通知为已读

### 课后管理模块
1. POST /api/task-assignments/submit — 学员提交课后任务
2. GET /api/task-assignments/student/{studentId}/submissions — 助教查看某个学员的任务提交记录
3. GET /api/task-assignments/task/{taskId}/submissions — 助教查看某个任务的所有提交记录
4. POST /api/task-assignments/grade — 助教批改打分任务
5. GET /api/task-assignments/student/{studentId}/stats — 获取学员任务统计数据
6. POST /api/tasks/publish — 课后任务发布
7. GET /api/tasks/student/{studentId}/list — 学员查看课后任务清单
8. GET /api/tasks/{taskId}/details — 查看任务详情
9. GET /api/tasks/published — 助教查看自己发布的任务
10. PUT /api/tasks/update — 助教修改已发布的任务
11. DELETE /api/tasks/{taskId} — 助教删除已发布的任务

### 模拟考试管理模块
1. POST /api/mock-exams/create — 创建模拟考试
2. POST /api/mock-exams/add-students — 添加学员并发送考试通知
3. GET /api/mock-exams/all — 助教查看所有考试列表
4. GET /api/mock-exams/my-list — 学员查看自己的考试列表
5. GET /api/mock-exams/detail — 查看考试详情
6. GET /api/mock-exams/paper — 学员查看试卷
7. POST /api/mock-exams/submit-answers — 学员提交考试答题信息
8. POST /api/mock-exams/auto-grade-objective — 系统自动批改客观题
9. DELETE /api/mock-exams/{examId} — 删除模拟考试
10. POST /api/exams/papers/create — 创建标准试卷