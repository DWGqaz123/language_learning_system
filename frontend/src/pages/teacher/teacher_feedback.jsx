import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './teacher_feedback.css'

const TeacherFeedbackManage = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [records, setRecords] = useState([])
  const [selectedRecord, setSelectedRecord] = useState(null)
  const [evaluation, setEvaluation] = useState('')

  useEffect(() => {
    fetchRecords()
  }, [])

  const fetchRecords = async () => {
    try {
      const res = await axios.get(`/api/courses/un-evaluated/${userId}`)
      setRecords(res.data.data || [])
    } catch (err) {
      alert('获取待评价记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleEvaluate = async () => {
    if (!evaluation.trim()) {
      alert('请输入课堂表现评价')
      return
    }
    try {
      await axios.post('/api/courses/evaluate-performance', {
        scheduleId: selectedRecord.scheduleId,
        studentId: selectedRecord.studentId,
        performanceEval: evaluation
      })
      alert('评价提交成功')
      setSelectedRecord(null)
      setEvaluation('')
      fetchRecords()
    } catch (err) {
      alert('提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const formatDateTime = (utcString) => {
    if (!utcString) return '-'
    const localTime = new Date(utcString)
    const year = localTime.getFullYear()
    const month = String(localTime.getMonth() + 1).padStart(2, '0')
    const day = String(localTime.getDate() + 1).padStart(2, '0') // 注意+1
    const hours = String(localTime.getHours()).padStart(2, '0')
    const minutes = String(localTime.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  }

  return (
    <div className="teacher-feedback-wrapper">
      <button className="back-button" onClick={() => navigate('/teacher/teacher-course')}>返回</button>
      <h2 className="title">待评价学员记录</h2>

      <div className="record-list">
        {records.length > 0 ? (
          records.map(r => (
            <div key={r.ssrId} className="record-card">
              <p><strong>学员：</strong>{r.studentName}</p>
              <p><strong>课程：</strong>{r.courseName}</p>
              <p><strong>上课时间：</strong>{formatDateTime(r.classTime)}</p>
              <p><strong>考勤状态：</strong>{r.attendStatus || '-'}</p>
              {r.leaveReason && <p><strong>请假理由：</strong>{r.leaveReason}</p>}
              <button className="evaluate-button" onClick={() => setSelectedRecord(r)}>评价</button>
            </div>
          ))
        ) : (
          <p className="no-data">暂无待评价记录</p>
        )}
      </div>

      {selectedRecord && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>评价学员课堂表现</h3>
            <textarea
              value={evaluation}
              onChange={(e) => setEvaluation(e.target.value)}
              placeholder="请输入课堂表现评价..."
            />
            <div className="modal-buttons">
              <button onClick={handleEvaluate}>提交评价</button>
              <button onClick={() => { setSelectedRecord(null); setEvaluation('') }}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TeacherFeedbackManage