import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './class_manage.css'
import { useNavigate } from 'react-router-dom'

const ClassManage = () => {
  const navigate = useNavigate()
  const [courses, setCourses] = useState([])
  const [studentsMap, setStudentsMap] = useState({})
  const [inputMap, setInputMap] = useState({})
  const [successMap, setSuccessMap] = useState({})
  const [errorMap, setErrorMap] = useState({})

  useEffect(() => {
    fetchCourses()
  }, [])

  const fetchCourses = async () => {
    try {
      const res = await axios.get('/api/courses/all')
      const classCourses = res.data.data.filter(c => c.courseType === '班级')
      setCourses(classCourses)

      classCourses.forEach(async course => {
        const res2 = await axios.get(`/api/courses/get-class-students?courseId=${course.courseId}`)
        setStudentsMap(prev => ({ ...prev, [course.courseId]: res2.data.data }))
      })
    } catch (err) {
      console.error('获取课程或学员失败', err)
    }
  }

  const handleInputChange = (courseId, value) => {
    setInputMap(prev => ({ ...prev, [courseId]: value }))
  }

  const handleAdd = async (courseId) => {
    setErrorMap(prev => ({ ...prev, [courseId]: '' }))
    setSuccessMap(prev => ({ ...prev, [courseId]: '' }))
    try {
      await axios.post('/api/courses/add-class-student', {
        courseId,
        studentIds: (inputMap[courseId] || '').split(',').map(id => Number(id.trim()))
      })
      setSuccessMap(prev => ({ ...prev, [courseId]: '添加成功' }))
      setInputMap(prev => ({ ...prev, [courseId]: '' }))
      fetchCourses()
    } catch (err) {
      setErrorMap(prev => ({
        ...prev,
        [courseId]: err.response?.data?.message || '添加失败'
      }))
    }
  }

  const handleRemove = async (courseId, studentId) => {
    if (!window.confirm('确认移除该学员？')) return
    try {
      await axios.delete('/api/courses/remove-class-student', {
        data: {
          courseId,
          studentIds: [studentId]  // ❗ 这里包成数组
        }
      })
      fetchCourses()
    } catch (err) {
      alert(err.response?.data?.message || '移除失败')
    }
  }

  return (
    <div className="class-manage-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-course')}>返回</button>
      <h2 className="title">班级管理</h2>

      {courses.map(course => (
        <div className="course-block" key={course.courseId}>
          <h3>{course.courseName}（课程ID: {course.courseId}, 班级编号: {course.classGroupCode}）</h3>
          <table className="student-table">
            <thead>
              <tr>
                <th>学员ID</th>
                <th>姓名</th>
                <th>手机号</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {(studentsMap[course.courseId] || []).map(student => (
                <tr key={student.studentId}>
                  <td>{student.studentId}</td>
                  <td>{student.username}</td>
                  <td>{student.phoneNumber}</td>
                  <td>
                    <button className="delete" onClick={() => handleRemove(course.courseId, student.studentId)}>移除</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="add-form">
            <input
              placeholder="输入学员ID，多个用英文逗号分隔"
              value={inputMap[course.courseId] || ''}
              onChange={(e) => handleInputChange(course.courseId, e.target.value)}
            />
            <button onClick={() => handleAdd(course.courseId)}>添加学员</button>
            {errorMap[course.courseId] && <p className="error">{errorMap[course.courseId]}</p>}
            {successMap[course.courseId] && <p className="success">{successMap[course.courseId]}</p>}
          </div>
        </div>
      ))}
    </div>
  )
}

export default ClassManage