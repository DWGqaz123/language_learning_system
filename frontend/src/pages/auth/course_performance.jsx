import React, { useEffect, useState } from 'react'
import './course_performance.css'

const CoursePerformance = () => {
  const [attendanceData, setAttendanceData] = useState(null)
  const [performanceList, setPerformanceList] = useState([])
  const [scheduleList, setScheduleList] = useState([])
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchAttendanceData()
    fetchPerformanceData()
    fetchScheduleData()
  }, [])

  const fetchAttendanceData = async () => {
    try {
      // 注意这里是需要 courseId 参数，不是 studentId
      // 这里简单假设取学员的第一门课程（实际项目中应该先查课程列表再取 courseId）
      const courseId = 1  // TODO: 请根据实际情况动态获取正确的 courseId
      const res = await fetch(`/api/courses/attendance-record?courseId=${courseId}`)
      const data = await res.json()
      setAttendanceData(data.data)
    } catch (error) {
      console.error('加载出勤数据失败', error)
    }
  }

  const fetchPerformanceData = async () => {
    try {
      const res = await fetch(`/api/courses/student/attendance-performance?studentId=${userInfo.userId}`)
      const data = await res.json()
      setPerformanceList(data.data || [])
    } catch (error) {
      console.error('加载课堂表现数据失败', error)
    }
  }

  const fetchScheduleData = async () => {
    try {
      const res = await fetch(`/api/courses/schedule-records/${userInfo.userId}`)
      const data = await res.json()
      setScheduleList(data.data || [])
    } catch (error) {
      console.error('加载排课记录失败', error)
    }
  }

  // 计算出勤率
  const calculateAttendanceRate = () => {
    if (!attendanceData || attendanceData.total === 0) return 0
    return ((attendanceData.attendCount / attendanceData.total) * 100).toFixed(1)
  }

  // 获取最新的课堂表现
  const getLatestPerformanceEval = () => {
    if (performanceList.length === 0) return ''
    // 假设以 classTime 最晚的一条作为最新
    const sortedList = [...performanceList].sort((a, b) => new Date(b.classTime) - new Date(a.classTime))
    return sortedList[0]?.performanceEval || ''
  }

  return (
    <div className="course-performance">
      <h2>课程表现分析</h2>

      <div className="attendance-section">
        <h3>出勤率</h3>
        <div className="attendance-chart">
          <p>
            {attendanceData
              ? `总体出勤率: ${calculateAttendanceRate()}%`
              : '加载中...'}
          </p>
        </div>
      </div>

      <div className="performance-section">
        <h3>课堂表现评价</h3>
        <div className="radar-chart">
          <p>
            {performanceList.length > 0
              ? `最新评价: ${getLatestPerformanceEval()}`
              : '加载中...'}
          </p>
        </div>
      </div>

      <div className="schedule-section">
        <h3>学习时间分布</h3>
        <div className="calendar-heatmap">
          <p>{scheduleList.length > 0 ? '学习记录加载完成' : '加载中...'}</p>
        </div>
      </div>
    </div>
  )
}

export default CoursePerformance