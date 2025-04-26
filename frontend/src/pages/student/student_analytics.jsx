import React, { useEffect, useState } from 'react'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Pie } from 'react-chartjs-2'
import axios from 'axios'
import './student_analytics.css'

ChartJS.register(ArcElement, Tooltip, Legend)

const StudentAnalytics = () => {
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [courseProgress, setCourseProgress] = useState(null)
  const [attendanceStats, setAttendanceStats] = useState(null)
  const [taskStats, setTaskStats] = useState(null)
  const [mockExamStats, setMockExamStats] = useState(null)
  const [studyRoomStats, setStudyRoomStats] = useState(null)

  useEffect(() => {
    fetchData()
  }, [])

  const fetchData = async () => {
    try {
      const [cpRes, atRes, tsRes, meRes, srRes] = await Promise.all([
        axios.get(`/api/analysis/course-progress?studentId=${userId}`),
        axios.get(`/api/analysis/attendance-stats?studentId=${userId}`),
        axios.get(`/api/analysis/task-statistics/${userId}`),
        axios.get(`/api/analysis/mock-exams/statistics/${userId}`),
        axios.get(`/api/study-rooms/usage-statistics/${userId}`)
      ])
      setCourseProgress(cpRes.data.data)
      setAttendanceStats(atRes.data.data)
      setTaskStats(tsRes.data.data)
      setMockExamStats(meRes.data.data)
      setStudyRoomStats(srRes.data.data)
    } catch (err) {
      alert('加载数据失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  if (!courseProgress || !attendanceStats || !taskStats || !mockExamStats || !studyRoomStats) {
    return <div className="layout"><main className="main"><p className="loading">加载中...</p></main></div>
  }

  const attendanceData = {
    labels: ['出勤', '请假', '缺勤'],
    datasets: [
      {
        data: [
          attendanceStats.attendCount,
          attendanceStats.leaveCount,
          attendanceStats.absentCount
        ],
        backgroundColor: ['#4caf50', '#ff9800', '#f44336'],
        borderWidth: 1
      }
    ]
  }

  return (
    <div className="layout">
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，{storedUser.username || '学员'}！</h2>
        </header>

        <section className="content">
          <div className="analytics-grid">
            <div className="card">
              <h3>课程进度</h3>
              <p>总课时：{courseProgress.totalHours}</p>
              <p>剩余课时：{courseProgress.remainingHours}</p>
              <div className="progress-bar">
                <div className="progress-fill" style={{ width: `${courseProgress.completedPercentage}%` }}>
                  {courseProgress.completedPercentage}%
                </div>
              </div>
            </div>

            <div className="card">
              <h3>考勤统计</h3>
              <Pie data={attendanceData} />
            </div>

            <div className="card">
              <h3>课后任务完成率</h3>
              <p>总任务数：{taskStats.totalTasks}</p>
              <p>已完成：{taskStats.submittedTasks}</p>
              <p>完成率：{taskStats.completionRate.toFixed(1)}%</p>
              <p>平均得分：{taskStats.averageScore != null ? taskStats.averageScore.toFixed(1) : '暂无'}</p>
            </div>

            <div className="card">
              <h3>模拟考试情况</h3>
              <p>考试次数：{mockExamStats.examCount}</p>
              <p>平均分：{mockExamStats.averageScore}</p>
            </div>

            <div className="card">
              <h3>自习室使用情况</h3>
              <p>累计使用：{studyRoomStats.totalUsageCount} 次</p>
              <p>上午使用：{studyRoomStats.morningCount} 次</p>
              <p>下午使用：{studyRoomStats.afternoonCount} 次</p>
              <p>晚上使用：{studyRoomStats.eveningCount} 次</p>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default StudentAnalytics