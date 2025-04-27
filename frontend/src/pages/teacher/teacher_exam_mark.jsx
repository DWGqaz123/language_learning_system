import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './teacher_exam_mark.css'

const TeacherExamMark = () => {
  const navigate = useNavigate()
  const [records, setRecords] = useState([])
  const [selectedRecord, setSelectedRecord] = useState(null)
  const [studentDetail, setStudentDetail] = useState(null)
  const [score, setScore] = useState('')
  const [comment, setComment] = useState('')

  useEffect(() => {
    fetchRecords()
  }, [])

  const fetchRecords = async () => {
    try {
      const res = await axios.get('/api/mock-exams/teacher/ungraded-subjective-records')
      setRecords(res.data.data || [])
    } catch (err) {
      alert('获取待批改试卷失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleSelectRecord = async (record) => {
    try {
      const res = await axios.get('/api/mock-exams/student-detail', {
        params: {
          examId: record.examId,
          studentId: record.studentId
        }
      })
      setStudentDetail(res.data.data)
      setSelectedRecord(record)
    } catch (err) {
      alert('获取学生答卷失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleSubmitGrade = async () => {
    if (score.trim() === '') {
      alert('请输入主观题得分')
      return
    }
    try {
      await axios.post('/api/mock-exams/grade-subjective', {
        examId: selectedRecord.examId,
        studentId: selectedRecord.studentId,
        subjectiveScore: Number(score),
        teacherComment: comment
      })
      alert('批改成功')
      setSelectedRecord(null)
      setStudentDetail(null)
      setScore('')
      setComment('')
      fetchRecords()
    } catch (err) {
      alert('批改失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const formatDateTime = (utcString) => {
    if (!utcString) return '-'
    const localTime = new Date(utcString)
    const year = localTime.getFullYear()
    const month = String(localTime.getMonth() + 1).padStart(2, '0')
    const day = String(localTime.getDate()).padStart(2, '0')
    const hours = String(localTime.getHours()).padStart(2, '0')
    const minutes = String(localTime.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  }

  return (
    <div className="teacher-exam-mark-wrapper">
      <button className="back-button" onClick={() => navigate('/teacher/teacher-exam')}>返回</button>
      <h2 className="title">待批改学员试卷</h2>

      <div className="teacher-exam-mark-list">
        {records.length > 0 ? (
          records.map(r => (
            <div key={r.examId + '-' + r.studentId} className="teacher-exam-mark-card">
              <p><strong>学员：</strong>{r.studentName}</p>
              <p><strong>考试：</strong>{r.examName}</p>
              <p><strong>考试时间：</strong>{formatDateTime(r.examTime)}</p>
              <button
                className="teacher-exam-mark-grade-button"
                onClick={() => handleSelectRecord(r)}
              >
                批改
              </button>
            </div>
          ))
        ) : (
          <p className="teacher-exam-mark-no-data">暂无待批改试卷</p>
        )}
      </div>

      {selectedRecord && studentDetail && (
        <div className="teacher-exam-mark-modal-overlay">
          <div className="teacher-exam-mark-modal-box">
            <h3>批改学员试卷</h3>

            <div className="subjective-question-list">
              <h4>主观题答卷</h4>
              {JSON.parse(studentDetail.subjectiveQuestionsJson || '[]').map((q, idx) => {
                const answers = JSON.parse(studentDetail.answersJson || '[]')
                const studentAnswer = answers.find(a => a.questionId === q.questionId)?.answer || '(未作答)'
                return (
                  <div key={q.questionId} className="subjective-question">
                    <p><strong>题目{idx + 1}：</strong>{q.questionText}</p>
                    <p><strong>学生作答：</strong>{studentAnswer}</p>
                  </div>
                )
              })}
            </div>

            <input
              className="teacher-exam-mark-input"
              type="number"
              placeholder="请输入主观题得分"
              value={score}
              onChange={(e) => setScore(e.target.value)}
            />
            <textarea
              className="teacher-exam-mark-textarea"
              placeholder="请输入老师评语（可选）"
              value={comment}
              onChange={(e) => setComment(e.target.value)}
            />
            <div className="teacher-exam-mark-modal-buttons">
              <button onClick={handleSubmitGrade}>提交批改</button>
              <button onClick={() => { setSelectedRecord(null); setStudentDetail(null); setScore(''); setComment('') }}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TeacherExamMark