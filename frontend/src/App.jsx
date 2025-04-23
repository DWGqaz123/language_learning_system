import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import LoginPage from './pages/auth/LoginPage'
import ActivateAccount from './pages/auth/activate_account'

import AssistantUserPage from './pages/assistant/assistant_user'
import RegisterStudent from './pages/assistant/register_student'
import UpdateInfo from './pages/assistant/update_info'
import AdminUser from './pages/admin/admin_user'
import ReviewUpdate from './pages/admin/review_update_request'





function App() {
  return (
    <Router>
      <Routes>
        {/* 登录页 */}
        <Route path="/" element={<LoginPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/activate-account" element={<ActivateAccount />} />


        {/* 管理员页面 */}
        <Route path="/admin-user" element={<AdminUser />} />
        <Route path="/admin/review-update-request" element={<ReviewUpdate />} />

        {/* 助教页面 */}
        <Route path="/assistant-user" element={<AssistantUserPage />} />
        <Route path="/assistant/register-student" element={<RegisterStudent />} />
        <Route path="/assistant/update-info" element={<UpdateInfo />} />

        {/* <Route path="/student-home" element={<StudentHome />} /> */}
        {/* <Route path="/teacher-home" element={<TeacherHome />} /> */}
        {/* <Route path="/admin-home" element={<AdminHome />} /> */}
      </Routes>
    </Router>
  )
}

export default App
