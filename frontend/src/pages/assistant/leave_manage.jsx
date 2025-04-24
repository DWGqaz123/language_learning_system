import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './leave_manage.css'
import { useNavigate } from 'react-router-dom'

const LeaveManage = () => {
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [schedulesMap, setSchedulesMap] = useState({})
  const [leaveRequests, setLeaveRequests] = useState([])
  const [selectedCourse, setSelectedCourse] = useState('')
  const [selectedSchedule, setSelectedSchedule] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    fetchCourses()
  }, [])

  const fetchCourses = async () => {
    try {
      const res = await axios.get('/api/courses/all')
      setCourses(res.data.data)
      res.data.data.forEach(async (course) => {
        const res2 = await axios.get(`/api/courses/${course.courseId}/schedules`)
        setSchedulesMap((prev) => ({ ...prev, [course.courseId]: res2.data.data }))
      })
    } catch (err) {
      console.error('获取课程或排课失败', err)
    }
  }

  const handleFetchLeaveRequests = async () => {
    if (!selectedCourse || !selectedSchedule) return
    try {
      const res = await axios.get(
        `/api/courses/${selectedCourse}/schedule/${selectedSchedule}/leave-requests`
      )
      setLeaveRequests(res.data.data)
      setMessage('')
    } catch (err) {
      setMessage('获取请假申请失败')
    }
  }

  const handleReview = async (ssrId, approved) => {
    const comment = window.prompt('请输入审核意见：')
    if (comment === null) return
    try {
      await axios.post('/api/courses/review-leave', {
        ssrId,
        approved,
        reviewComment: comment
      })
      setMessage('审核成功')
      handleFetchLeaveRequests()
    } catch (err) {
      setMessage(err.response?.data?.message || '审核失败')
    }
  }

  const getStatusText = (status) => {
    switch (status) {
      case '待批':
      case 'PENDING':
        return '待审批'
      case '请假':
      case 'APPROVED':
        return '已通过'
      case '未开始':
      case 'REJECTED':
        return '已驳回'
      default:
        return status || '未知'
    }
  }

  return (
    <div className="leave-manage-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-course')}>
        返回
      </button>
      <h2 className="title">请假审批管理</h2>

      <div className="selector">
        <select value={selectedCourse} onChange={(e) => setSelectedCourse(e.target.value)}>
          <option value="">选择课程</option>
          {courses.map((course) => (
            <option key={course.courseId} value={course.courseId}>
              {course.courseName}（ID: {course.courseId}）
            </option>
          ))}
        </select>

        <select value={selectedSchedule} onChange={(e) => setSelectedSchedule(e.target.value)}>
          <option value="">选择课表</option>
          {(schedulesMap[selectedCourse] || []).map((s) => (
            <option key={s.scheduleId} value={s.scheduleId}>
              {s.classTime}（ID: {s.scheduleId}）
            </option>
          ))}
        </select>

        <button className='search' onClick={handleFetchLeaveRequests}>查询请假申请</button>
      </div>

      {message && (
        <p className={message.includes('成功') ? 'success' : 'error'}>{message}</p>
      )}

      <table className="leave-table">
        <thead>
          <tr>
            <th>学员ID</th>
            <th>姓名</th>
            <th>课程</th>
            <th>上课时间</th>
            <th>请假原因</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {leaveRequests.map((r) => (
            <tr key={r.ssrId}>
              <td>{r.studentId}</td>
              <td>{r.studentName}</td>
              <td>{r.courseName}</td>
              <td>{r.classTime}</td>
              <td>{r.leaveReason}</td>
              <td>{getStatusText(r.attendStatus)}</td>
              <td>
                <button className="approve" onClick={() => handleReview(r.ssrId, true)}>通过</button>
                <button className="reject" onClick={() => handleReview(r.ssrId, false)}>驳回</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default LeaveManage