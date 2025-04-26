import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_exam_report.css'

const StudentExamReport = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [examRecords, setExamRecords] = useState([])
  const [selectedReport, setSelectedReport] = useState(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    fetchExamRecords()
  }, [])

  const fetchExamRecords = async () => {
    try {
        const res = await axios.get(`/api/mock-exams/my-list`, {
            params: { studentId: userId } // 注意：是 studentId 不是 userId
          })
      setExamRecords(res.data.data || [])
    } catch (err) {
      alert('获取考试记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleViewReport = async (examId) => {
    try {
      const res = await axios.get(`/api/mock-exams/exam-report`, {
        params: { examId, studentId: userId }
      })
      setSelectedReport(res.data.data)
      setShowModal(true)
    } catch (err) {
      alert('获取成绩报告失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="exam-report-wrapper">
        <button className="back-button" onClick={() => navigate('/student/student-exam')}>← 返回</button>
        <h2 className="title">我的考试成绩</h2>

      {examRecords.length > 0 ? (
        <div className="exam-card-grid">
          {examRecords.map(record => (
            <div key={record.examId} className="exam-card">
              <h3>{record.examName}</h3>
              <p><strong>考试时间：</strong>{record.examTime?.replace('T', ' ')}</p>
              <p><strong>状态：</strong>{record.examStatus || '未知'}</p>
              <p><strong>总分：</strong>{record.totalScore != null ? record.totalScore : '未出分'}</p>

              {record.examStatus === 'ended' ? (
                <button className="view-btn" onClick={() => handleViewReport(record.examId)}>查看报告</button>
              ) : (
                <button className="view-btn disabled" disabled>未完成</button>
              )}
            </div>
          ))}
        </div>
      ) : (
        <p className="no-exam-records">暂无考试记录</p>
      )}

      {/* 成绩报告模态框 */}
      {showModal && selectedReport && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>成绩报告</h3>
            <p><strong>考试名称：</strong>{selectedReport.examName}</p>
            <p><strong>总分：</strong>{selectedReport.totalScore != null ? selectedReport.totalScore : '未出分'}</p>
            <p><strong>客观题得分：</strong>{selectedReport.objectiveScore != null ? selectedReport.objectiveScore : '未批改'}</p>
            <p><strong>主观题得分：</strong>{selectedReport.subjectiveScore != null ? selectedReport.subjectiveScore : '未批改'}</p>
            <p><strong>教师点评：</strong>{selectedReport.teacherComment || '暂无'}</p>
            <p><strong>助教点评：</strong>{selectedReport.assistantComment || '暂无'}</p>

            <div className="modal-buttons">
              <button id="close-btn" onClick={() => setShowModal(false)}>关闭</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default StudentExamReport