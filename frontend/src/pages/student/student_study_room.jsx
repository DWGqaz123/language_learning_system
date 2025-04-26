import React from 'react'
import { Link } from 'react-router-dom'
import './student.css'

const StudentStudyRoom = () => {
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  return (
    <div className="student-layout">
      <aside className="sider">
        <div className="avatar">
          <div className="circle">学</div>
          <div className="label">学员: {storedUser.username || '...'}</div>
          <div className="label">用户ID: {storedUser.userId || '...'}</div>
          <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/student/student-user">个人中心</Link></li>
          <li><Link to="/student/student-course">课程管理</Link></li>
          <li><Link to="/student/student-task">课后任务管理</Link></li>
          <li><Link to="/student/student-exam">模拟考试管理</Link></li>
          <li className="active"><Link to="/student/student-study-room">自习室管理</Link></li>
          <li><Link to="/student/student-resource">资源管理</Link></li>
          <li><Link to="/student/student-analytics">学习分析</Link></li>
          <li><Link to="/student/student-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，{storedUser.username || '学员'}！请选择下方卡片进入自习室模块。</h2>
        </header>

        <section className="content">
          <div className="card-grid">
            <div className="card">
              <h3>明日自习室列表和预约</h3>
              <p>查看明日可以预约的自习室，并进行预约操作。</p>
              <Link to="/student/student-study-room-list">立即进入</Link>
            </div>
            <div className="card">
              <h3>预约记录与签到签退</h3>
              <p>查看自己历史和当前的自习室预约信息。
                <br />查看签到签退记录，进行自习室签到和签退操作。
              </p>
              <Link to="/student/student-study-room-records">立即进入</Link>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default StudentStudyRoom