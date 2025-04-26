import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './task_publish.css'
import { useNavigate } from 'react-router-dom'

const TaskPublish = () => {
  const navigate = useNavigate()
  const [userInfo, setUserInfo] = useState(null)
  const [taskType, setTaskType] = useState('课后作业')
  const [courses, setCourses] = useState([])
  const [schedules, setSchedules] = useState([])
  const [form, setForm] = useState({
    scheduleId: '',
    studentId: '',
    taskContent: '',
    deadline: ''
  })
  const [message, setMessage] = useState('')

  useEffect(() => {
    const phone = sessionStorage.getItem('phone')
    if (phone) {
      axios.get('/api/users/phone', { params: { phoneNumber: phone } })
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data)
          }
        })
    }
  }, [])

  useEffect(() => {
    if (userInfo) {
      axios.get(`/api/courses/my-schedules`, { params: { userId: userInfo.userId } })
        .then(res => {
          setSchedules(res.data.data || [])
        })
        .catch(() => setMessage('获取排课失败'))
    }
  }, [userInfo])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async () => {
    if (!form.taskContent || !form.deadline) {
      setMessage('请填写完整任务内容与截止时间')
      return
    }

    const dto = {
      taskType,
      taskContent: form.taskContent,
      deadline: form.deadline,
      publisherId: userInfo.userId,
      scheduleId: taskType === '课后作业' ? Number(form.scheduleId) : null,
      studentId: taskType === '训练任务' ? Number(form.studentId) : null
    }

    try {
      await axios.post('/api/tasks/publish', dto)
      setMessage('任务发布成功！')
      setForm({ scheduleId: '', studentId: '', taskContent: '', deadline: '' })
    } catch (err) {
      setMessage(err.response?.data?.message || '任务发布失败')
    }
  }

  return (
    <div className="task-publish-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-task')}>返回</button>
      

      <div className="card">
      <h2 className="title">发布课后任务</h2>
        <label>任务类型</label>
        <select value={taskType} onChange={(e) => setTaskType(e.target.value)}>
          <option value="课后作业">课后作业</option>
          <option value="训练任务">训练任务</option>
        </select>

        {taskType === '课后作业' && (
          <>
            <label>选择课表</label>
            <select name="scheduleId" value={form.scheduleId} onChange={handleChange}>
              <option value="">请选择课表</option>
              {schedules.map(s => (
                <option key={s.scheduleId} value={s.scheduleId}>
                  {s.courseName} - {s.classTime}
                </option>
              ))}
            </select>
          </>
        )}

        {taskType === '训练任务' && (
          <>
            <label>学员ID</label>
            <input
              name="studentId"
              value={form.studentId}
              onChange={handleChange}
              placeholder="请输入学员ID"
            />
          </>
        )}

        <label>任务内容</label>
        <textarea
          name="taskContent"
          value={form.taskContent}
          onChange={handleChange}
          placeholder="请填写任务内容"
        />

        <label>截止时间</label>
        <input
          type="datetime-local"
          name="deadline"
          value={form.deadline}
          onChange={handleChange}
        />

        {message && <p className="message">{message}</p>}

        <button className="submit-button" onClick={handleSubmit}>发布任务</button>
      </div>
    </div>
  )
}

export default TaskPublish