import React, { useEffect, useState } from 'react'
import './exam_list.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const ExamList = () => {
  const [exams, setExams] = useState([])
  const [papers, setPapers] = useState([])
  const [rooms, setRooms] = useState([])
  const [editExam, setEditExam] = useState(null)
  const [showEditModal, setShowEditModal] = useState(false)

  const [selectedExamId, setSelectedExamId] = useState(null)
  const [studentRecords, setStudentRecords] = useState([])
  const [showRecordsModal, setShowRecordsModal] = useState(false)

  const [selectedDetail, setSelectedDetail] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)

  const [commentInputs, setCommentInputs] = useState({}) // ✅改为记录每个学生自己的点评内容

  const navigate = useNavigate()

  useEffect(() => {
    fetchExams()
    fetchPapers()
    fetchRooms()
  }, [])

  const fetchExams = async () => {
    try {
      const res = await axios.get('/api/mock-exams/all')
      setExams(res.data.data || [])
    } catch (err) {
      alert('获取考试列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const fetchPapers = async () => {
    const res = await axios.get('/api/exams/papers/standard-papers')
    setPapers(res.data.data || [])
  }

  const fetchRooms = async () => {
    const res = await axios.get('/api/study-rooms/all')
    setRooms(res.data.data || [])
  }

  const handleDelete = async (examId) => {
    if (!window.confirm('确定要删除该考试吗？')) return
    try {
      const res = await axios.delete(`/api/mock-exams/${examId}`)
      const { code, message } = res.data
  
      if (code === 200) {
        alert('删除成功')
        fetchExams()
      } else {
        if (message.includes('无法')) {
          alert('无法删除：考试已有关联学生记录')
        } else {
          alert('删除失败：' + message)
        }
      }
    } catch (err) {
      const msg = err.response?.data?.message || '未知错误'
      alert('删除请求失败: ' + msg)
    }
  }

  const openEditModal = (exam) => {
    setEditExam({
      examId: exam.examId,
      examName: exam.examName,
      examTime: exam.examTime?.slice(0, 16),
      standardPaperId: papers.find(p => p.paperName === exam.paperName)?.paperId || '',
      examRoomId: rooms.find(r => r.roomName === exam.examRoomName)?.roomId || ''
    })
    setShowEditModal(true)
  }

  const handleEdit = async () => {
    try {
      await axios.put('/api/mock-exams/update', {
        ...editExam,
        examTime: new Date(editExam.examTime).toISOString()
      })
      alert('更新成功')
      setShowEditModal(false)
      fetchExams()
    } catch (err) {
      alert('更新失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openRecordsModal = async (examId) => {
    setSelectedExamId(examId)
    try {
      const res = await axios.get(`/api/mock-exams/records?examId=${examId}`)
      setStudentRecords(res.data.data || [])
      setCommentInputs({}) // 打开新的考试记录时清空之前的点评输入
      setShowRecordsModal(true)
    } catch (err) {
      alert('获取考试记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openDetailModal = async (examId, studentId) => {
    try {
      const res = await axios.get(`/api/mock-exams/student-detail?examId=${examId}&studentId=${studentId}`)
      setSelectedDetail(res.data.data || null)
      setShowDetailModal(true)
    } catch (err) {
      alert('获取答卷详情失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleSubmitComment = async (studentId) => {
    const comment = commentInputs[studentId] || ''
    if (!comment.trim()) return alert('请输入点评内容')
    try {
      await axios.post('/api/mock-exams/assistant-comment', {
        examId: selectedExamId,
        studentId,
        assistantComment: comment
      })
      alert('点评提交成功')
      openRecordsModal(selectedExamId) // 刷新学员记录
    } catch (err) {
      alert('点评失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleCommentChange = (studentId, value) => {
    setCommentInputs(prev => ({
      ...prev,
      [studentId]: value
    }))
  }

  const getStatus = (record) => {
    if (!record.completedTime) return '未开始'
    if (record.totalScore != null) return '已批改'
    return '已完成'
  }

  return (
    <div className="exam-list-wrapper">
                <button className='back-button' onClick={() => navigate('/assistant/assistant-exam')}>返回</button>
      <h2 className='title' >模拟考试列表</h2>

      <table className="exam-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>考试名称</th>
            <th>考试时间</th>
            <th>试卷</th>
            <th>教室</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {exams.map(exam => (
            <tr key={exam.examId}>
              <td>{exam.examId}</td>
              <td>{exam.examName}</td>
              <td>{exam.examTime}</td>
              <td>{exam.paperName}</td>
              <td>{exam.examRoomName}</td>
              <td>
                <button onClick={() => openEditModal(exam)}>编辑</button>
                <button onClick={() => handleDelete(exam.examId)}>删除</button>
                <button onClick={() => openRecordsModal(exam.examId)}>学员记录</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 编辑弹窗 */}
      {showEditModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>编辑考试</h3>
            <div className="form-group"><label>考试名称：</label>
              <input value={editExam.examName} onChange={e => setEditExam(prev => ({ ...prev, examName: e.target.value }))} />
            </div>
            <div className="form-group"><label>考试时间：</label>
              <input type="datetime-local" value={editExam.examTime} onChange={e => setEditExam(prev => ({ ...prev, examTime: e.target.value }))} />
            </div>
            <div className="form-group"><label>试卷：</label>
              <select value={editExam.standardPaperId} onChange={e => setEditExam(prev => ({ ...prev, standardPaperId: e.target.value }))}>
                <option value="">请选择</option>
                {papers.map(p => <option key={p.paperId} value={p.paperId}>{p.paperName}</option>)}
              </select>
            </div>
            <div className="form-group"><label>考试教室：</label>
              <select value={editExam.examRoomId} onChange={e => setEditExam(prev => ({ ...prev, examRoomId: e.target.value }))}>
                <option value="">请选择</option>
                {rooms.map(r => <option key={r.roomId} value={r.roomId}>{r.roomName}</option>)}
              </select>
            </div>
            <div className="modal-buttons">
              <button onClick={handleEdit}>保存</button>
              <button onClick={() => setShowEditModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 学员考试记录模态框 */}
      {showRecordsModal && (
        <div className="modal-overlay">
          <div className="modal-box wide">
            <h3>学员考试记录</h3>
            <table className="record-table">
              <thead>
                <tr>
                  <th>学员ID</th>
                  <th>完成时间</th>
                  <th>总分</th>
                  <th>主观题</th>
                  <th>考试状态</th>
                  <th>教师评语</th>
                  <th>助教点评</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {studentRecords.map(s => (
                  <tr key={s.studentId}>
                    <td>{s.studentId}</td>
                    <td>{s.completedTime || '-'}</td>
                    <td>{s.totalScore ?? '-'}</td>
                    <td>{s.subjectiveScore ?? '-'}</td>
                    <td>{getStatus(s)}</td>
                    <td>{s.teacherComment || '-'}</td>
                    <td>{s.assistantComment || '-'}</td>
                    <td>
                    <button onClick={() => handleSubmitComment(s.studentId)}>提交点评</button>
                    <button onClick={() => openDetailModal(s.examId, s.studentId)}>查看答卷</button>
                      <input
                        placeholder="助教点评"
                        value={commentInputs[s.studentId] || ''}
                        onChange={e => handleCommentChange(s.studentId, e.target.value)}
                      />
       
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <button className='close' onClick={() => setShowRecordsModal(false)}>关闭</button>
          </div>
        </div>
      )}

      {/* 答卷详情模态框 */}
      {showDetailModal && selectedDetail && (
        <div className="modal-overlay">
          <div className="modal-box wide">
            <h3>学员答卷详情</h3>
            <pre style={{ maxHeight: '400px', overflowY: 'auto', background: '#f9f9f9', padding: '10px' }}>
              {JSON.stringify(JSON.parse(selectedDetail.answersJson || '{}'), null, 2)}
            </pre>
            <button className='close' onClick={() => setShowDetailModal(false)}>关闭</button>
          </div>
        </div>
      )}
    </div>
  )
}

export default ExamList