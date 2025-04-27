import React from 'react'
import { Link } from 'react-router-dom'
import './teacher_analytics.css'

const TeacherAnalyticsManage = () => {
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  return (
    <div className="teacher-layout">
      {/* 左侧导航栏 */}
      <aside className="sider">
        <div className="avatar">
          <div className="circle">教</div>
          <div className="label">教师: {storedUser.username || '...'}</div>
          <div className="label">用户ID: {storedUser.userId || '...'}</div>
          <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/teacher/teacher-user">个人中心</Link></li>
          <li><Link to="/teacher/teacher-course">课程管理</Link></li>
          <li><Link to="/teacher/teacher-task">课后管理</Link></li>
          <li><Link to="/teacher/teacher-exam">模拟考试管理</Link></li>
          <li><Link to="/teacher/teacher-study-room">自习室管理</Link></li>
          <li><Link to="/teacher/teacher-resource">资源管理</Link></li>
          <li className="active"><Link to="/teacher/teacher-analytics">学习分析</Link></li>
          <li><Link to="/teacher/teacher-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      {/* 主内容区 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，教师 {storedUser.username || '...'}！</h2>
        </header>

        <section className="teacher-analytics-wrapper">
          <h2 className="teacher-analytics-wrapper-title">学习分析模块</h2>
          <p className="teacher-analytics-wrapper-desc">（本模块正在开发中，敬请期待...）</p>
        </section>
      </main>
    </div>
  )
}

export default TeacherAnalyticsManage