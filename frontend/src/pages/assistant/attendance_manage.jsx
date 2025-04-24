import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './attendance_manage.css'
import { useNavigate } from 'react-router-dom'

const AttendanceManage = () => {
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [scheduleMap, setScheduleMap] = useState({})
  const [selectedCourseId, setSelectedCourseId] = useState('')
  const [studentStatuses, setStudentStatuses] = useState([])
  const [selectedScheduleId, setSelectedScheduleId] = useState('')
  const [attendanceStats, setAttendanceStats] = useState(null)
  const [currentUserId, setCurrentUserId] = useState('')
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    // 直接从 localStorage 读取当前用户
    const userInfo = JSON.parse(localStorage.getItem('userInfo'))
    if (userInfo) {
      setCurrentUserId(userInfo.userId)
    }
    fetchCourses()
  }, [])

  const fetchCourses = async () => {
    try {
      const res = await axios.get('/api/courses/all')
      setCourses(res.data.data)
      res.data.data.forEach(async (course) => {
        const res2 = await axios.get(`/api/courses/${course.courseId}/schedules`)
        setScheduleMap((prev) => ({
          ...prev,
          [course.courseId]: res2.data.data
        }))
      })
    } catch (err) {
      console.error('获取课程或排课失败', err)
    }
  }

  const handleScheduleSelect = async (courseId, scheduleId) => {
    try {
      setSelectedCourseId(courseId)
      setSelectedScheduleId(scheduleId)
      setShowModal(true)

      const res = await axios.get(`/api/courses/schedule/${scheduleId}/students-status`)
      setStudentStatuses(res.data.data)

      const stat = await axios.get(`/api/courses/attendance-record`, {
        params: { courseId }
      })
      setAttendanceStats(stat.data.data)
    } catch (err) {
      console.error('获取数据失败', err)
    }
  }

  const handleStatusChange = async (studentId, newStatus) => {
    try {
      await axios.post(`/api/courses/update-attendance-status`, {
        scheduleId: selectedScheduleId,
        studentId,
        attendStatus: newStatus
      })
      handleScheduleSelect(selectedCourseId, selectedScheduleId)
    } catch (err) {
      alert('更新失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="attendance-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-course')}>返回</button>
      <h2 className="title">出勤与表现管理</h2>

      {courses.map(course => (
        <div key={course.courseId} className="course-block">
          <h3>{course.courseName}（ID: {course.courseId}, 班级编号: {course.classGroupCode || '无'}）</h3>
          <div className="schedules">
            {(scheduleMap[course.courseId] || [])
              .filter(schedule => schedule.assistantId === currentUserId)
              .map(schedule => (
                <button
                  key={schedule.scheduleId}
                  onClick={() => handleScheduleSelect(course.courseId, schedule.scheduleId)}
                  className={selectedScheduleId === schedule.scheduleId ? 'active' : ''}
                >
                  {schedule.classTime}
                </button>
              ))}
          </div>
        </div>
      ))}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>学生出勤情况</h3>
            <button className="close-button" onClick={() => setShowModal(false)}>关闭</button>

            {attendanceStats && (
              <div className="stats">
                <p>总人数：{attendanceStats.total}</p>
                <p>出勤：{attendanceStats.attendCount}</p>
                <p>缺勤：{attendanceStats.absentCount}</p>
                <p>请假：{attendanceStats.leaveCount}</p>
              </div>
            )}

            <table className="attendance-table">
              <thead>
                <tr>
                  <th>学员ID</th>
                  <th>姓名</th>
                  <th>当前状态</th>
                  <th>请假原因</th>
                  <th>课堂评价</th>
                  <th>更改状态</th>
                </tr>
              </thead>
              <tbody>
                {studentStatuses.map(student => (
                  <tr key={student.studentId}>
                    <td>{student.studentId}</td>
                    <td>{student.studentName}</td>
                    <td>{student.attendStatus}</td>
                    <td>{student.leaveReason || '-'}</td>
                    <td>{student.performanceEval || '-'}</td>
                    <td>
                      <select
                        value={student.attendStatus}
                        onChange={(e) => handleStatusChange(student.studentId, e.target.value)}
                      >
                        <option value="未开始">未开始</option>
                        <option value="出勤">出勤</option>
                        <option value="缺勤">缺勤</option>
                        <option value="请假">请假</option>
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

export default AttendanceManage