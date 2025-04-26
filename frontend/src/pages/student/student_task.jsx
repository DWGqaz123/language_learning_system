import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import './student.css' // 复用 student.css 样式

const StudentTask = () => {
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const [userInfo, setUserInfo] = useState(null)

  useEffect(() => {
    if (storedUser.userId) {
      axios.get(`/api/users/get-by-id?userId=${storedUser.userId}`)
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data)
          }
        })
        .catch(err => {
          console.error('获取用户信息失败:', err)
        })
    }
  }, [storedUser.userId])

  return (
    <div className="student-layout">
      {/* 左侧导航栏 */}
      <aside className="sider">
        <div className="avatar">
          <div className="circle">学</div>
          <div className="label">学员: {userInfo ? userInfo.username : '...'}</div>
          <div className="label">用户ID: {userInfo ? userInfo.userId : '...'}</div>
          <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/student/student-user">个人中心</Link></li>
          <li><Link to="/student/student-course">课程管理</Link></li>
          <li className="active"><Link to="/student/student-task">课后任务管理</Link></li>
          <li><Link to="/student/student-exam">模拟考试管理</Link></li>
          <li><Link to="/student/student-study-room">自习室管理</Link></li>
          <li><Link to="/student/student-resource">资源管理</Link></li>
          <li><Link to="/student/student-analytics">学习分析</Link></li>
          <li><Link to="/student/student-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      {/* 主内容区域 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，学员 {userInfo ? userInfo.username : '...'}！请选择下方卡片进入任务模块。</h2>
        </header>

        <section className="content">
          <div className="card-grid">
            <div className="card">
              <h3>任务清单</h3>
              <p>查看并完成您的课后任务。
                <br />请注意任务截止日期，及时提交作业。
                <br />查看您的任务提交记录和反馈。
              </p>
              <Link to="/student/task-list">进入查看</Link>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default StudentTask