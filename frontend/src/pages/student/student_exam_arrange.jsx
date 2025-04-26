import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_exam_arrange.css'

const StudentExamArrange = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [examList, setExamList] = useState([])

  useEffect(() => {
    fetchExamList()
  }, [])

  const fetchExamList = async () => {
    try {
      const res = await axios.get(`/api/mock-exams/my-list`, {
        params: { studentId: userId }
      })
      setExamList(res.data.data || [])
    } catch (err) {
      alert('获取考试安排失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const isExamStarted = (examTime) => {
    if (!examTime) return false
    const now = new Date()
    const startTime = new Date(examTime)
    return now >= startTime
  }

  const isExamCompleted = (completedTime) => {
    return completedTime != null
  }

  return (
    <div className="exam-arrange-wrapper">
        <button className="back-button" onClick={() => navigate('/student/student-exam')}>返回</button>
        <h2 className='title' >考试安排与考试</h2>

      <div className="exam-grid">
        {examList.length > 0 ? (
          examList.map(exam => {
            const completed = isExamCompleted(exam.completedTime)
            const started = isExamStarted(exam.examTime)

            return (
              <div key={exam.examId} className={`exam-card ${completed ? 'completed' : ''}`}>
                <h3>{exam.examName}</h3>
                <p><strong>考试时间：</strong>{exam.examTime?.replace('T', ' ') || '-'}</p>
                <p><strong>考试状态：</strong>{completed ? '已完成' : (started ? '可进入考试' : '未开始')}</p>

                {!completed && started && (
                    <button onClick={() => navigate(`/student/student-start-exam/${exam.examId}`)}>进入考试</button>
                )}
              </div>
            )
          })
        ) : (
          <p className="no-exams">暂无考试安排</p>
        )}
      </div>
    </div>
  )
}

export default StudentExamArrange