import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './task_list.css'

const TaskList = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [tasks, setTasks] = useState([])
  const [selectedDetailTask, setSelectedDetailTask] = useState(null)
  const [showDetailModal, setShowDetailModal] = useState(false)

  const [selectedSubmitTask, setSelectedSubmitTask] = useState(null)
  const [showSubmitModal, setShowSubmitModal] = useState(false)
  const [submitText, setSubmitText] = useState('')
  const [submitFile, setSubmitFile] = useState(null)

  const [submissionRecord, setSubmissionRecord] = useState(null)
  const [showRecordModal, setShowRecordModal] = useState(false)

  useEffect(() => {
    fetchTasks()
  }, [])

  const fetchTasks = async () => {
    try {
      const res = await axios.get(`/api/tasks/student/${userId}/list`)
      const rawTasks = res.data.data || []

      const sorted = [...rawTasks].sort((a, b) => {
        const statusOrder = (status) => {
          if (status === '已完成') return 2
          if (status === '已过期') return 2
          return 1 // 未完成优先
        }
        const diff = statusOrder(a.completionStatus) - statusOrder(b.completionStatus)
        if (diff !== 0) return diff

        const dateA = new Date(a.deadline)
        const dateB = new Date(b.deadline)
        return dateA - dateB
      })

      setTasks(sorted)
    } catch (err) {
      alert('获取任务列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openDetailModal = async (taskId) => {
    try {
      const res = await axios.get(`/api/tasks/${taskId}/details`, {
        params: { studentId: userId }
      })
      setSelectedDetailTask(res.data.data || null)
      setShowDetailModal(true)
    } catch (err) {
      alert('获取任务详情失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openSubmitModal = async (taskId) => {
    try {
      const res = await axios.get(`/api/tasks/${taskId}/details`, {
        params: { studentId: userId }
      })
      setSelectedSubmitTask(res.data.data || null)
      setShowSubmitModal(true)
    } catch (err) {
      alert('获取任务详情失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openRecordModal = async (taskId) => {
    try {
      const res = await axios.get(`/api/task-assignments/student/${userId}/task/${taskId}`, {
        params: { studentId: userId }
      })
      setSubmissionRecord(res.data.data || null)
      setShowRecordModal(true)
    } catch (err) {
      alert('获取提交记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleFileChange = (e) => {
    setSubmitFile(e.target.files[0])
  }

  const handleSubmitTask = async () => {
    if (!selectedSubmitTask) return

    const formData = new FormData()
    formData.append('taskId', selectedSubmitTask.taskId)
    formData.append('studentId', userId)
    if (submitFile) formData.append('attachmentFile', submitFile)
    if (submitText.trim()) formData.append('submitText', submitText.trim())

    try {
      await axios.post('/api/task-assignments/submit', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      alert('任务提交成功')
      setShowSubmitModal(false)
      fetchTasks()
    } catch (err) {
      alert('提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="task-list-wrapper">
      <button className="back-button" onClick={() => navigate('/student/student-task')}>← 返回</button>
      <h2 className="title">我的课后任务</h2>

      <div className="task-grid">
        {tasks.length > 0 ? (
          tasks.map(task => {
            const isCompletedOrExpired = task.completionStatus === '已完成' || task.completionStatus === '已过期'
            return (
              <div
                key={task.taskId}
                className={`task-card ${isCompletedOrExpired ? 'completed' : ''}`}
              >
                <h3>{task.taskType}</h3>
                <p><strong>发布时间：</strong>{task.publishTime?.replace('T', ' ')}</p>
                <p><strong>截止时间：</strong>{task.deadline?.replace('T', ' ')}</p>
                <p><strong>完成状态：</strong>{task.completionStatus || '未完成'}</p>
                <div className="button-group">
                  <button onClick={() => openDetailModal(task.taskId)}>查看详情</button>
                  <button
                    onClick={() => openSubmitModal(task.taskId)}
                    disabled={isCompletedOrExpired}
                    className={isCompletedOrExpired ? 'disabled-btn' : ''}
                  >
                    提交任务
                  </button>
                  <button onClick={() => openRecordModal(task.taskId)}>查看提交记录</button>
                </div>
              </div>
            )
          })
        ) : (
          <p className="no-tasks">暂无任务</p>
        )}
      </div>

      {/* 查看详情模态框 */}
      {showDetailModal && selectedDetailTask && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>任务详情</h3>
            <p><strong>任务类型：</strong>{selectedDetailTask.taskType}</p>
            <p><strong>任务内容：</strong>{selectedDetailTask.taskContent}</p>
            <p><strong>发布时间：</strong>{selectedDetailTask.publishTime?.replace('T', ' ')}</p>
            <p><strong>截止时间：</strong>{selectedDetailTask.deadline?.replace('T', ' ')}</p>

            <div className="modal-buttons">
              <button id='tl_closeBtn' onClick={() => setShowDetailModal(false)}>关闭</button>
            </div>
          </div>
        </div>
      )}

      {/* 提交任务模态框 */}
      {showSubmitModal && selectedSubmitTask && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>提交任务</h3>
            <p><strong>任务类型：</strong>{selectedSubmitTask.taskType}</p>
            <p><strong>任务内容：</strong>{selectedSubmitTask.taskContent}</p>

            <textarea
              placeholder="输入提交文本（可选）"
              value={submitText}
              onChange={(e) => setSubmitText(e.target.value)}
            />

            <input
              type="file"
              onChange={handleFileChange}
            />

            <div className="modal-buttons">
              <button onClick={handleSubmitTask}>确认提交</button>
              <button onClick={() => setShowSubmitModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 查看提交记录模态框 */}
      {showRecordModal && submissionRecord && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>提交记录</h3>
            <p><strong>提交时间：</strong>{submissionRecord.submitTime?.replace('T', ' ') || '暂无'}</p>
            <p><strong>提交内容：</strong>{submissionRecord.submitText || '暂无提交文本'}</p>
            <p><strong>得分：</strong>{submissionRecord.score != null ? submissionRecord.score : '未评分'}</p>
            <p><strong>评语：</strong>{submissionRecord.gradeComment || '暂无评语'}</p>

            <div className="modal-buttons">
              <button id='tl_closeBtn' onClick={() => setShowRecordModal(false)}>关闭</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TaskList