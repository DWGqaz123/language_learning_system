import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'

import LoginPage from './pages/auth/LoginPage'
import ActivateAccount from './pages/auth/activate_account'

//管理员
import AdminUser from './pages/admin/admin_user'
import ReviewUpdate from './pages/admin/review_update_request'
import RegisterEmployee from './pages/admin/register_employee'
import RoleManagement from './pages/admin/role_management'
import AdminStudyRoom from './pages/admin/admin_study_room'
import AdminStudyRoomList from './pages/admin/admin_study_room_list'
import AdminStudyRoomReview from './pages/admin/admin_study_room_review'
import AdminResource from './pages/admin/admin_resource'
import AdminResourceCategory from './pages/admin/admin_resource_category'
import AdminResourceList from './pages/admin/admin_resource_list'
import AdminResourceAudit from './pages/admin/admin_resource_audit'


//助教
import AssistantUserPage from './pages/assistant/assistant_user'
import RegisterStudent from './pages/assistant/register_student'
import UpdateInfo from './pages/assistant/update_info'
import AssistantCourse from './pages/assistant/assistant_course'
import CourseManage from './pages/assistant/course_manage'
import ScheduleManage from './pages/assistant/schedule_manage'
import ClassManage from './pages/assistant/class_manage'
import LeaveManage from './pages/assistant/leave_manage'
import AttendanceManage from './pages/assistant/attendance_manage'
import AssistantTask from './pages/assistant/assistant_task'
import TaskPublish from './pages/assistant/task_publish'
import TaskDetail from './pages/assistant/task_detail'
import AssistantExam from './pages/assistant/assistant_exam'
import ExamCreate from './pages/assistant/exam_create'
import ExamList from './pages/assistant/exam_list'
import ExamPaperCreate from './pages/assistant/exam_paper_create'
import ExamPaperList from './pages/assistant/exam_paper_list'
import AssistantStudyRoom from './pages/assistant/assistant_study_room'
import AssistantResource from './pages/assistant/assistant_resource'
import AssistantAnalytics from './pages/assistant/assistant_analytics'
import AssistantNotification from './pages/assistant/assistant_notification'

//学员
import StudentUser from './pages/student/student_user'
import StudentUpdateInfo from './pages/student/student_update_info'
import StudentCourse from './pages/student/student_course'
import StudentScheduleManage from './pages/student/student_schedule_manage'
import StduentCourseList from './pages/student/student_course_list'
import StudentFeedback from './pages/student/student_feedback'
import StudentTask from './pages/student/student_task'
import TaskList from './pages/student/task_list'
import StudentExam from './pages/student/student_exam';
import StudentExamArrange from './pages/student/student_exam_arrange';
import StudentStartExam from './pages/student/student_start_exam';
import StudentExamReport from './pages/student/student_exam_report';
import StudentStudyRoom from './pages/student/student_study_room'
import StudentStudyRoomList from './pages/student/student_study_room_list'
import StudentStudyRoomRecords from './pages/student/student_study_room_records'
import StudentResource from './pages/student/student_resource'
import StudentResourceList from './pages/student/student_resource_list'
import StudentAnalytics from './pages/student/student_analytics'
import StudentNotification from './pages/student/student_notification'
import TeacherUpdateInfo from './pages/teacher/teacher_update_info'
//教师
import TeacherUser from './pages/teacher/teacher_user'
import TeacherCourse from './pages/teacher/teacher_course'
import TeacherScheduleManage from './pages/teacher/teacher_schedule_manage'
import TeacherCourseList from './pages/teacher/teacher_course_list'
import TeacherFeedback from './pages/teacher/teacher_feedback'
import TeacherTask from './pages/teacher/teacher_task'
import TeacherExam from './pages/teacher/teacher_exam'
import TeacherExamMark from './pages/teacher/teacher_exam_mark'
import TeacherStudyRoom from './pages/teacher/teacher_study_room'
import TeacherResourceManage from './pages/teacher/teacher_resource'
import TeacherAnalyticsManage from './pages/teacher/teacher_analytics'
import TeacherNotificationManage from './pages/teacher/teacher_notification'


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
        <Route path="/admin/admin-study-room" element={<AdminStudyRoom />} />
        <Route path="/admin/admin-study-room-list" element={<AdminStudyRoomList />} />
        <Route path="/admin/admin-study-room-review" element={<AdminStudyRoomReview />} />
        <Route path="/admin/admin-resource" element={<AdminResource />} />
        <Route path="/admin/admin-resource-category" element={<AdminResourceCategory />} />
        <Route path="/admin/admin-resource-list" element={<AdminResourceList />} />
        <Route path="/admin/admin-resource-audit" element={<AdminResourceAudit />} />


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
        <Route path="/assistant/assistant-task" element={<AssistantTask />} />
        <Route path="/assistant/task-publish" element={<TaskPublish />} />
        <Route path="/assistant/task-detail" element={<TaskDetail />} />
        <Route path="/assistant/assistant-exam" element={<AssistantExam />} />
        <Route path="/assistant/exam-paper-create" element={<ExamPaperCreate />} />
        <Route path="/assistant/exam-paper-list" element={<ExamPaperList />} />
        <Route path="/assistant/exam-create" element={<ExamCreate />} />
        <Route path="/assistant/exam-list" element={<ExamList />} />
        <Route path="/assistant/assistant-study-room" element={<AssistantStudyRoom />} />
        <Route path="/assistant/assistant-resource" element={<AssistantResource />} />
        <Route path="/assistant/assistant-analytics" element={<AssistantAnalytics />} />
        <Route path="/assistant/assistant-notification" element={<AssistantNotification />} />

        {/* 学员模块 */}
        <Route path="/student/student-user" element={<StudentUser />} />
        <Route path="/student/update-info" element={<StudentUpdateInfo />} />
        <Route path="/student/student-course" element={<StudentCourse />} />
        <Route path="/student/student-schedule-manage" element={<StudentScheduleManage />} />
        <Route path="/student/student-course-list" element={<StduentCourseList />} />
        <Route path="/student/student-feedback" element={<StudentFeedback />} />
        <Route path="/student/student-task" element={<StudentTask />} />
        <Route path="/student/task-list" element={<TaskList />} />
        <Route path="/student/student-exam" element={<StudentExam />} />
        <Route path="/student/student-exam-arrange" element={<StudentExamArrange />} />
        <Route path="/student/student-start-exam/:examId" element={<StudentStartExam />} />
        <Route path="/student/student-exam-report" element={<StudentExamReport />} />
        <Route path="/student/student-study-room" element={<StudentStudyRoom />} />
        <Route path="/student/student-study-room-list" element={<StudentStudyRoomList />} />
        <Route path="/student/student-study-room-records" element={<StudentStudyRoomRecords />} />
        <Route path="/student/student-resource" element={<StudentResource />} />
        <Route path="/student/student-resource-list" element={<StudentResourceList />} />
        <Route path="/student/student-analytics" element={<StudentAnalytics />} />
        <Route path="/student/student-notification" element={<StudentNotification />} />

        {/* 教师模块 */}
        <Route path="/teacher/teacher-user" element={<TeacherUser />} />
        <Route path="/teacher/teacher-update-info" element={<TeacherUpdateInfo />} />
        <Route path="/teacher/teacher-course" element={<TeacherCourse />} />
        <Route path="/teacher/teacher-course-list" element={<TeacherCourseList />} />
        <Route path="/teacher/teacher-schedule-manage" element={<TeacherScheduleManage />} />
        <Route path="/teacher/teacher-feedback" element={<TeacherFeedback />} />
        <Route path="/teacher/teacher-task" element={<TeacherTask />} />
        <Route path="/teacher/teacher-exam" element={<TeacherExam />} />
        <Route path="/teacher/teacher-exam-mark" element={<TeacherExamMark />} />
        <Route path="/teacher/teacher-study-room" element={<TeacherStudyRoom />} />
        <Route path="/teacher/teacher-resource" element={<TeacherResourceManage />} />
        <Route path="/teacher/teacher-analytics" element={<TeacherAnalyticsManage />} />
        <Route path="/teacher/teacher-notification" element={<TeacherNotificationManage />} />
      </Routes>
    </Router>
  )
}

export default App
