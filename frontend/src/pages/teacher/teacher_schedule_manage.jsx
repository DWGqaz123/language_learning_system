import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './teacher_schedule_manage.css'

const TeacherScheduleManage = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [scheduleRecords, setScheduleRecords] = useState([])

  useEffect(() => {
    fetchScheduleRecords()
  }, [])

  const fetchScheduleRecords = async () => {
    try {
      const res = await axios.get('/api/courses/my-schedules', {
        params: { userId: userId }
      })
      setScheduleRecords(res.data.data || [])
    } catch (err) {
      alert('获取课表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="schedule-wrapper">
      <button className="back-button" onClick={() => navigate('/teacher/teacher-course')}>返回</button>
      <h2 className="title">查看个人课表</h2>

      <div className="schedule-table-wrapper">
        <table className="schedule-table">
          <thead>
            <tr>
              <th>上课时间</th>
              <th>课程名称</th>
              <th>教室</th>
              <th>教师</th>
              <th>助教</th>
            </tr>
          </thead>
          <tbody>
            {scheduleRecords.length > 0 ? (
              scheduleRecords.map(record => (
                <tr key={record.scheduleId}>
                  <td>{record.classTime?.replace('T', ' ') || '-'}</td>
                  <td>{record.courseName || '-'}</td>
                  <td>{record.roomName || '-'}</td>
                  <td>{record.teacherName || '-'}</td>
                  <td>{record.assistantName || '-'}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="5" className="no-data">暂无排课安排</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default TeacherScheduleManage