import React, { useEffect, useState } from 'react'
import './exam_paper_list.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const ExamPaperList = () => {
  const navigate = useNavigate()
  const [papers, setPapers] = useState([])
  const [editPaper, setEditPaper] = useState(null)
  const [showEditModal, setShowEditModal] = useState(false)
  const [detailPaper, setDetailPaper] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)

  useEffect(() => {
    fetchPapers()
  }, [])

  const fetchPapers = async () => {
    try {
      const res = await axios.get('/api/exams/papers/standard-papers')
      setPapers(res.data.data || [])
    } catch (err) {
      alert('获取试卷列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleDelete = async (paperId) => {
    if (!window.confirm('确定要删除该试卷吗？')) return
    try {
      await axios.delete(`/api/exams/papers/${paperId}`)
      alert('删除成功')
      fetchPapers()
    } catch (err) {
      alert('删除失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openEditModal = async (paper) => {
    try {
      const res = await axios.get(`/api/exams/papers/${paper.paperId}`)
      const fullPaper = res.data.data
      setEditPaper({
        paperId: fullPaper.paperId,
        paperName: fullPaper.paperName,
        examType: fullPaper.examType,
        paperContentJson: fullPaper.paperContentJson || '',
        objectiveAnswersJson: fullPaper.objectiveAnswersJson || ''
      })
      setShowEditModal(true)
    } catch (err) {
      alert('加载试卷内容失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleEdit = async () => {
    try {
      await axios.put('/api/exams/papers/editPaper', {
        ...editPaper
      })
      alert('更新成功')
      setShowEditModal(false)
      fetchPapers()
    } catch (err) {
      alert('更新失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openDetailModal = async (paperId) => {
    try {
      const res = await axios.get(`/api/exams/papers/${paperId}`)
      setDetailPaper(res.data.data || null)
      setShowDetailModal(true)
    } catch (err) {
      alert('查看详情失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="exam-paper-list-wrapper">
        <button className='back-button' onClick={() => navigate('/assistant/assistant-exam')}>返回</button>
      <h2 className='title'>标准试卷列表</h2>

      <table className="exam-paper-table">
        <thead>
          <tr>
            <th>试卷ID</th>
            <th>试卷名称</th>
            <th>考试类型</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {papers.map(paper => (
            <tr key={paper.paperId}>
              <td>{paper.paperId}</td>
              <td>{paper.paperName}</td>
              <td>{paper.examType}</td>
              <td>{paper.createdTime}</td>
              <td>
                <button onClick={() => openDetailModal(paper.paperId)}>查看详情</button>
                <button onClick={() => openEditModal(paper)}>编辑</button>
                <button onClick={() => handleDelete(paper.paperId)}>删除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 编辑试卷弹窗 */}
      {showEditModal && editPaper && (
        <div className="modal-overlay">
          <div className="modal-box large">
            <h3>编辑试卷</h3>
            <div className="form-group">
              <label>试卷名称：</label>
              <input
                value={editPaper.paperName}
                onChange={e => setEditPaper(prev => ({ ...prev, paperName: e.target.value }))}
              />
            </div>
            <div className="form-group">
              <label>考试类型：</label>
              <input
                value={editPaper.examType}
                onChange={e => setEditPaper(prev => ({ ...prev, examType: e.target.value }))}
              />
            </div>
            <div className="form-group">
              <label>试卷内容（JSON格式）：</label>
              <textarea
                rows={8}
                value={editPaper.paperContentJson}
                onChange={e => setEditPaper(prev => ({ ...prev, paperContentJson: e.target.value }))}
              />
            </div>
            <div className="form-group">
              <label>客观题答案（JSON格式）：</label>
              <textarea
                rows={5}
                value={editPaper.objectiveAnswersJson}
                onChange={e => setEditPaper(prev => ({ ...prev, objectiveAnswersJson: e.target.value }))}
              />
            </div>
            <div className="modal-buttons">
              <button onClick={handleEdit}>保存</button>
              <button onClick={() => setShowEditModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 查看详情弹窗 */}
      {showDetailModal && detailPaper && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>试卷详情</h3>
            <pre className="detail-json">
              {JSON.stringify(JSON.parse(detailPaper.paperContentJson || '{}'), null, 2)}
            </pre>
            <button onClick={() => setShowDetailModal(false)}>关闭</button>
          </div>
        </div>
      )}
    </div>
  )
}

export default ExamPaperList