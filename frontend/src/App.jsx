import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import LoginPage from './pages/auth/LoginPage'
import ActivateAccount from './pages/auth/activate_account'

import AssistantUserPage from './pages/assistant/assistant_user'
import RegisterStudent from './pages/assistant/register_student'
import UpdateInfo from './pages/assistant/update_info'
import AssistantCourse from './pages/assistant/assistant_course'
import CourseManage from './pages/assistant/course_manage'
import ScheduleManage from './pages/assistant/schedule_manage'
import ClassManage from './pages/assistant/class_manage'
import LeaveManage from './pages/assistant/leave_manage'
import AttendanceManage from './pages/assistant/attendance_manage'


import AdminUser from './pages/admin/admin_user'
import ReviewUpdate from './pages/admin/review_update_request'
import RegisterEmployee from './pages/admin/register_employee'
import RoleManagement from './pages/admin/role_management'

function App() {
  return (
    <Router>
      <Routes>
        {/* 登录相关 */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/activate-account" element={<ActivateAccount />} />

        {/* 管理员模块 */}
        <Route path="/admin/admin-user" element={<AdminUser />} />
        <Route path="/admin/review-update-request" element={<ReviewUpdate />} />
        <Route path="/admin/register-employee" element={<RegisterEmployee />} />
        <Route path="/admin/role-management" element={<RoleManagement />} />

        {/* 助教模块 */}
        <Route path="/assistant/assistant-user" element={<AssistantUserPage />} />
        <Route path="/assistant/register-student" element={<RegisterStudent />} /> 
        <Route path="/assistant/update-info" element={<UpdateInfo />} />
        <Route path="/assistant/assistant-course" element={<AssistantCourse />} />
        <Route path="/assistant/course-manage" element={<CourseManage />} />
        <Route path="/assistant/schedule-manage" element={<ScheduleManage />} />
        <Route path="/assistant/class-manage" element={<ClassManage />} />
        <Route path="/assistant/leave-manage" element={<LeaveManage />} /> 
        <Route path="/assistant/attendance-manage" element={<AttendanceManage />} />
      </Routes>
    </Router>
  )
}

export default App
