import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './teacher_course_list.css'

const TeacherCourseList = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [courses, setCourses] = useState([])

  useEffect(() => {
    fetchCourses()
  }, [])

  const fetchCourses = async () => {
    try {
      const res = await axios.get(`/api/courses/my-courses`, {
        params: { userId }
      })
      setCourses(res.data.data || [])
    } catch (err) {
      alert('获取课程失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const getProgress = (total, remaining) => {
    if (!total || total === 0) return '0%'
    return Math.round(((total - remaining) / total) * 100) + '%'
  }

  const groupedCourses = {
    '班级': courses.filter(c => c.courseType === '班级'),
    '1对1': courses.filter(c => c.courseType === '1对1')
  }

  return (
    <div className="course-list-wrapper">
      <button className="back-button" onClick={() => navigate('/teacher/teacher-course')}>返回</button>
      <h2 className="title">查看个人课程</h2>

      <div className="course-content">
        {Object.keys(groupedCourses).map(type => (
          <div key={type} className="course-type-section">
            <h3>{type}课程</h3>

            {groupedCourses[type].length > 0 ? (
              <div className="course-grid">
                {groupedCourses[type].map(course => (
                  <div key={course.courseId} className="student-course-card">
                    <h4>{course.courseName}</h4>
                    <p><strong>班级编号：</strong>{course.classGroupCode || '-'}</p>
                    <p><strong>总课时：</strong>{course.totalHours ?? '-'}</p>
                    <p><strong>剩余课时：</strong>{course.remainingHours ?? '-'}</p>
                    <p><strong>课程进度：</strong>{getProgress(course.totalHours, course.remainingHours)}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="no-courses">暂无{type}课程</p>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}

export default TeacherCourseList