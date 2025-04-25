import React, { useEffect, useState } from 'react'
import './task_detail.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const TaskDetail = () => {
  const [tasks, setTasks] = useState([])
  const [selectedTask, setSelectedTask] = useState(null)
  const [submissions, setSubmissions] = useState([])
  const [gradingForm, setGradingForm] = useState({})
  const [editForm, setEditForm] = useState({})
  const [showSubmissionsModal, setShowSubmissionsModal] = useState(false)
  const [showEditModal, setShowEditModal] = useState(false)
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const navigate = useNavigate()
  useEffect(() => {
    fetchTasks()
  }, [])

  const fetchTasks = async () => {
    try {
      const res = await axios.get(`/api/tasks/published?publisherId=${userInfo.userId}`)
      setTasks(res.data.data || [])
    } catch (err) {
      console.error('获取任务失败', err)
    }
  }

  const openSubmissionsModal = async (task) => {
    setSelectedTask(task)
    try {
      const res = await axios.get(`/api/task-assignments/task/${task.taskId}/submissions`)
      setSubmissions(res.data.data || [])
      setShowSubmissionsModal(true)
    } catch (err) {
      console.error('获取提交记录失败', err)
    }
  }

  const handleGrade = async () => {
    try {
      await axios.post('/api/tasks/grade', {
        ...gradingForm,
        graderId: userInfo.userId,
        score: Number(gradingForm.score),
      })
      alert('评分成功')
      openSubmissionsModal(selectedTask)
    } catch (err) {
      alert('评分失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openGradingForm = (taskId, studentId) => {
    setGradingForm({ taskId, studentId, score: '', gradeComment: '' })
  }

  const handleNotify = async (studentId, studentName) => {
    try {
      await axios.post('/api/notifications/send', {
        receiverId: studentId,
        notificationType: '任务提醒',
        refTargetId: selectedTask.taskId,
        refTargetType: '任务',
        content: `请尽快完成任务：${selectedTask.taskContent}`,
      })
      alert(`已向 ${studentName} 发送提醒`)
    } catch (err) {
      alert('发送失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }


  const openEditModal = (task) => {
    setEditForm({
      taskId: task.taskId,
      taskContent: task.taskContent,
      deadline: task.deadline?.slice(0, 16) || '',
    })
    setShowEditModal(true)
  }

  const handleEdit = async () => {
    try {
      await axios.put('/api/tasks/update', {
        ...editForm,
        deadline: new Date(editForm.deadline).toISOString()
      })
      alert('任务更新成功')
      setShowEditModal(false)
      fetchTasks()
    } catch (err) {
      alert('更新失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleDelete = async (taskId) => {
    if (!window.confirm('确认删除该任务？')) return
    try {
      await axios.delete(`/api/tasks/${taskId}`)
      fetchTasks()
    } catch (err) {
      alert('删除失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="task-detail-wrapper">
      <button className="back-button" onClick={() => navigate(-1)}>返回</button>
      <h2 className="title">已发布任务详情</h2>
      <table className="task-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>类型</th>
            <th>内容</th>
            <th>发布时间</th>
            <th>截止时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map(task => (
            <tr key={task.taskId}>
              <td>{task.taskId}</td>
              <td>{task.taskType}</td>
              <td>{task.taskContent}</td>
              <td>{task.publishTime}</td>
              <td>{task.deadline}</td>
              <td>
                <button onClick={() => openSubmissionsModal(task)}>查看提交</button>
                <button onClick={() => openEditModal(task)}>编辑</button>
                <button onClick={() => handleDelete(task.taskId)}>删除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showSubmissionsModal && (
        <div className="modal-overlay">
          <div className="modal-box" id='submissionModal'>
            <h3>提交记录 - 任务ID: {selectedTask.taskId}</h3>
            <button className="close-button" onClick={() => setShowSubmissionsModal(false)}>关闭</button>
            <table className="submission-table">
              <thead>
                <tr>
                  <th>学员ID</th>
                  <th>姓名</th>
                  <th>状态</th>
                  <th>提交时间</th>
                  <th>附件</th>
                  <th>文本</th>
                  <th>分数</th>
                  <th>评语</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                {submissions.map(s => (
                  <tr key={s.studentId}>
                    <td>{s.studentId}</td>
                    <td>{s.studentName}</td>
                    <td>{s.completionStatus}</td>
                    <td>{s.submitTime || '-'}</td>
                    <td>
                      {s.attachmentPath
                        ? <a href={s.attachmentPath} target="_blank" rel="noreferrer">查看</a>
                        : '-'}
                    </td>
                    <td>{s.submitText || '-'}</td>
                    <td>{s.score || '-'}</td>
                    <td>{s.gradeComment || '-'}</td>
                    <td>
                      {s.completionStatus === '未完成' ? (
                        <button id='messageBtn' onClick={() => handleNotify(s.studentId, s.studentName)}>发送提醒</button>
                      ) : (
                        <button onClick={() => openGradingForm(s.taskId, s.studentId)}>评分</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {gradingForm.taskId && (
              <div className="grading-box">
                <h4>批改评分</h4>
                <input
                  type="number"
                  placeholder="分数"
                  value={gradingForm.score}
                  onChange={(e) => setGradingForm({ ...gradingForm, score: e.target.value })}
                />
                <input
                  placeholder="评语"
                  value={gradingForm.gradeComment}
                  onChange={(e) => setGradingForm({ ...gradingForm, gradeComment: e.target.value })}
                />
                <button onClick={handleGrade}>提交评分</button>
              </div>
            )}
          </div>
        </div>
      )}

      {showEditModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>编辑任务</h3>
            <div className="edit-form">
              <label>任务内容：</label>
              <textarea
                value={editForm.taskContent}
                onChange={(e) => setEditForm(prev => ({ ...prev, taskContent: e.target.value }))}
              />
              <label>截止时间：</label>
              <input
                type="datetime-local"
                value={editForm.deadline}
                onChange={(e) => setEditForm(prev => ({ ...prev, deadline: e.target.value }))}
              />
            </div>
            <div className="modal-buttons">
              <button onClick={handleEdit}>保存修改</button>
              <button onClick={() => setShowEditModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TaskDetail