import React, { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import axios from 'axios'
import './student_start_exam.css'

const StudentStartExam = () => {
  const navigate = useNavigate()
  const { examId } = useParams()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [paperContent, setPaperContent] = useState([])
  const [paperName, setPaperName] = useState('')
  const [answers, setAnswers] = useState([])

  useEffect(() => {
    fetchExamPaper()
  }, [])

  const fetchExamPaper = async () => {
    try {
      const res = await axios.get('/api/mock-exams/paper', {
        params: { examId, studentId: userId }
      })
      const paperData = res.data.data
      if (paperData) {
        setPaperName(paperData.paperName)
        if (paperData.paperContentJson) {
          setPaperContent(JSON.parse(paperData.paperContentJson))
        }
      }
    } catch (err) {
      alert('获取试卷失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleAnswerChange = (questionId, value) => {
    setAnswers(prev => {
      const updated = prev.filter(a => a.questionId !== questionId)
      updated.push({ questionId, answer: value })
      return updated
    })
  }

  const getSelectedAnswer = (questionId) => {
    const found = answers.find(a => a.questionId === questionId)
    return found ? found.answer : ''
  }

  const handleSubmitExam = async () => {
    if (!window.confirm('确认提交考试？')) return

    try {
      await axios.post('/api/mock-exams/submit-answers', {
        examId,
        studentId: userId,
        answersJson: JSON.stringify(answers)
      })

      await axios.post('/api/mock-exams/auto-grade-objective', null, {
        params: { examId, studentId: userId }
      })

      alert('考试提交成功，客观题已批改！')
      navigate('/student/student-exam-arrange')
    } catch (err) {
      alert('提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  if (!paperContent.length) {
    return <div className="exam-wrapper"><p>加载中...</p></div>
  }

  return (
    <div className="exam-wrapper">
      <button className="back-button" onClick={() => navigate('/student/student-exam-arrange')}>← 返回</button>
      <h2 className="title">开始考试：{paperName || '未命名试卷'}</h2>

      <div className="question-list">
        {paperContent.map((q, index) => (
          <div key={q.questionId} className="question-card">
            <h4>第{index + 1}题：{q.questionText}</h4>

            {q.type === 'objective' ? (
              <div className="options">
                {['A', 'B', 'C', 'D'].map(opt => (
                  q[`option${opt}`] && (
                    <div
                      key={opt}
                      className={`option-item ${getSelectedAnswer(q.questionId) === opt ? 'selected' : ''}`}
                      onClick={() => handleAnswerChange(q.questionId, opt)}
                    >
                      {opt}. {q[`option${opt}`]}
                    </div>
                  )
                ))}
              </div>
            ) : (
              <textarea
                placeholder="请输入你的答案"
                value={getSelectedAnswer(q.questionId)}
                onChange={(e) => handleAnswerChange(q.questionId, e.target.value)}
              />
            )}
          </div>
        ))}
      </div>

      <div className="submit-area">
        <button className="submit-btn" onClick={handleSubmitExam}>提交考试</button>
      </div>
    </div>
  )
}

export default StudentStartExam