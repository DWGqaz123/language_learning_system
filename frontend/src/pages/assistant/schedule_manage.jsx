import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './schedule_manage.css'
import { useNavigate } from 'react-router-dom'

const ScheduleManage = () => {
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [schedulesMap, setSchedulesMap] = useState({})
  const [showModal, setShowModal] = useState(false)
  const [editMode, setEditMode] = useState(false)
  const [rooms, setRooms] = useState([])
  const [form, setForm] = useState({
    scheduleId: null,
    courseId: '',
    classTime: '',
    teacherId: '',
    assistantId: '',
    roomId: ''
  })

  useEffect(() => {
    fetchCourses()
    fetchRooms()
  }, [])

  const fetchCourses = async () => {
    try {
      const res = await axios.get('/api/courses/all')
      setCourses(res.data.data)
      res.data.data.forEach(async course => {
        const res2 = await axios.get(`/api/courses/${course.courseId}/schedules`)
        setSchedulesMap(prev => ({ ...prev, [course.courseId]: res2.data.data }))
      })
    } catch (err) {
      console.error('获取课程或课表失败', err)
    }
  }

  const fetchRooms = async () => {
    try {
      const res = await axios.get('/api/study-rooms')
      setRooms(res.data.data)
    } catch (err) {
      console.error('获取教室失败', err)
    }
  }

  const openCreateModal = (courseId) => {
    setEditMode(false)
    setForm({
      scheduleId: null,
      courseId,
      classTime: '',
      teacherId: '',
      assistantId: '',
      roomId: ''
    })
    setShowModal(true)
  }

  const openEditModal = async (scheduleId) => {
    try {
      const res = await axios.get(`/api/courses/schedules/${scheduleId}`)
      const data = res.data.data
      const courseObj = courses.find(c => c.courseName === data.courseName)
      setEditMode(true)
      setForm({
        scheduleId: data.scheduleId,
        courseId: courseObj?.courseId || '',
        classTime: data.classTime,
        teacherId: data.teacherId || '',
        assistantId: data.assistantId || '',
        roomId: data.roomId || ''
      })
      setShowModal(true)
    } catch (err) {
      console.error('获取排课详情失败', err)
    }
  }

  const handleDelete = async (scheduleId) => {
    if (!window.confirm('确认删除该排课？')) return
    try {
      await axios.delete(`/api/schedules/${scheduleId}/delete`)
      fetchCourses()
    } catch (err) {
      alert(err.response?.data?.message || '删除失败')
    }
  }

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async () => {
    try {
      const classTimeFormatted = new Date(form.classTime).toISOString()

      if (editMode) {
        await axios.put('/api/schedules/update', {
          scheduleId: form.scheduleId,
          classTime: classTimeFormatted,
          teacherId: Number(form.teacherId),
          assistantId: form.assistantId ? Number(form.assistantId) : null,
          roomId: Number(form.roomId)
        })
        alert('排课修改成功')
      } else {
        await axios.post('/api/schedules/create', {
          courseId: form.courseId,
          classTime: classTimeFormatted,
          teacherId: Number(form.teacherId),
          assistantId: form.assistantId ? Number(form.assistantId) : null,
          roomId: Number(form.roomId)
        })
        alert('排课成功，已自动生成学员记录并发送通知')
      }

      setShowModal(false)
      fetchCourses()
    } catch (err) {
      const errorMsg = err.response?.data?.message || '操作失败，请检查时间或教室是否冲突'
      alert(`排课失败：${errorMsg}`)
    }
  }

  return (
    <div className="schedule-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-course')}>返回</button>
      <h2 className="title">排课管理</h2>

      {courses.map(course => (
        <div className="course-card" key={course.courseId}>
          <div className="course-header">
            <h3>
              {course.courseType === '班级'
                ? `${course.courseName}（班级） - 班级编号: ${course.classGroupCode || '无'}`
                : `${course.courseName}（1对1）`}
            </h3>
            <p>ID: {course.courseId}</p>
            {course.courseType === '1对1' && course.studentId && (
              <p>学员ID: {course.studentId}</p>
            )}
            <button className="create-button" onClick={() => openCreateModal(course.courseId)}>+ 新建排课</button>
          </div>
          <table className="schedule-table">
            <thead>
              <tr>
                <th>上课时间</th>
                <th>教师</th>
                <th>助教</th>
                <th>教室</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {(schedulesMap[course.courseId] || []).map(schedule => (
                <tr key={schedule.scheduleId}>
                  <td>{schedule.classTime}</td>
                  <td>{schedule.teacherName || '-'}</td>
                  <td>{schedule.assistantName || '-'}</td>
                  <td>{schedule.roomName || '-'}</td>
                  <td>
                    <button className="edit" onClick={() => openEditModal(schedule.scheduleId)}>编辑</button>
                    <button className="delete" onClick={() => handleDelete(schedule.scheduleId)}>删除</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}

      {showModal && (
        <div className="modal-overlay">
          <div className="modal_course">
            <h3>{editMode ? '编辑排课' : '新建排课'}</h3>
            <input type="datetime-local" name="classTime" value={form.classTime} onChange={handleInputChange} />
            <input name="teacherId" placeholder="教师ID" value={form.teacherId} onChange={handleInputChange} />
            <input name="assistantId" placeholder="助教ID（可为空）" value={form.assistantId} onChange={handleInputChange} />
            <select name="roomId" value={form.roomId} onChange={handleInputChange}>
              <option value="">请选择教室</option>
              {rooms.map(room => (
                <option key={room.roomId} value={room.roomId}>
                  {room.roomName}（{room.roomId}）
                </option>
              ))}
            </select>
            <div className="modal-buttons">
              <button onClick={handleSubmit}>提交</button>
              <button onClick={() => setShowModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default ScheduleManage