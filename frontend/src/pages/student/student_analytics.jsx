import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import './student_analytics.css'

// 引入学习分析子模块组件
import DashBoard from '../auth/dash_board'
import CoursePerformance from '../auth/course_performance'
import LearningReports from '../auth/learning_reports'
import StudyRoomUsage from '../auth/study_room_usage'  // ⬅️ 加入新组件

ChartJS.register(ArcElement, Tooltip, Legend)

const StudentAnalytics = () => {
  const [userInfo, setUserInfo] = useState({})

  useEffect(() => {
    const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
    setUserInfo(storedUser)
  }, [])

  return (
    <div className="student-layout">
      {/* 左侧导航栏 */}
      <aside className="sider">
        <div className="avatar">
          <div className="circle">学</div>
          <div className="label">学员: {userInfo.username || '...'}</div>
          <div className="label">用户ID: {userInfo.userId || '...'}</div>
          <div className="label">账号: {userInfo.phoneNumber || '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/student/student-user">个人中心</Link></li>
          <li><Link to="/student/student-course">课程管理</Link></li>
          <li><Link to="/student/student-task">课后任务管理</Link></li>
          <li><Link to="/student/student-exam">模拟考试管理</Link></li>
          <li><Link to="/student/student-study-room">自习室管理</Link></li>
          <li><Link to="/student/student-resource">资源管理</Link></li>
          <li className="active"><Link to="/student/student-analytics">学习分析</Link></li>
          <li><Link to="/student/student-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      {/* 主内容区 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，{userInfo.username || '学员'}！</h2>
        </header>

        <section className="content">
          {/* 学习分析模块内容开始 */}

          {/* 1. 学习概览仪表盘 */}
          <DashBoard />

          {/* 2. 课程表现分析 + 自习室使用分析（并排两栏） */}
          <div className="analysis-columns">
            <CoursePerformance />
            <StudyRoomUsage />  {/* ✅ 这里补上 */}
          </div>

          {/* 3. 学习报告与建议 */}
          <LearningReports />

          {/* 学习分析模块内容结束 */}
        </section>
      </main>
    </div>
  )
}

export default StudentAnalytics