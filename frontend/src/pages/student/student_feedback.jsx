import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_feedback.css'

const StudentFeedback = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [records, setRecords] = useState([])

  useEffect(() => {
    fetchRecords()
  }, [])

  const fetchRecords = async () => {
    try {
      const res = await axios.get(`/api/courses/schedule-records/${userId}`)
      // 这里加上筛选，只保留 attendStatus !== '未开始' 的记录
      const filteredRecords = (res.data.data || []).filter(record => record.attendStatus !== '未开始')
      setRecords(filteredRecords)
    } catch (err) {
      alert('获取学习反馈失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const formatTime = (timeStr) => {
    if (!timeStr) return '-'
    return timeStr.replace('T', ' ').slice(0, 16)
  }

  return (
    <div className="feedback-wrapper">
        <button className="back-button" onClick={() => navigate('/student/student-course')}>返回</button>
        <h2 className='title' >查看学习反馈</h2>
      {records.length > 0 ? (
        <div className="record-grid">
          {records.map(record => (
            <div key={record.ssrId} className="record-card">
              <h3>{record.courseName || '-'}</h3>
              <p><strong>上课时间：</strong>{formatTime(record.classTime)}</p>
              <p><strong>教室：</strong>{record.roomName || '-'}</p>
              <p><strong>教师：</strong>{record.teacherName || '-'}</p>
              <p><strong>助教：</strong>{record.assistantName || '-'}</p>
              <p><strong>出勤状态：</strong>{record.attendStatus || '-'}</p>
              <p><strong>课堂表现：</strong>{record.performanceEval || '-'}</p>
            </div>
          ))}
        </div>
      ) : (
        <p className="no-records">暂无学习反馈记录</p>
      )}
    </div>
  )
}

export default StudentFeedback