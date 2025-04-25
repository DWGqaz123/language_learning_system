import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './assistant.css'
import { Link } from 'react-router-dom'

const AssistantStudyRoom = () => {
  const phone = localStorage.getItem('phone') || ''
  const [userInfo, setUserInfo] = useState(null)

  useEffect(() => {
    if (phone) {
      axios.get(`/api/users/phone`, { params: { phoneNumber: phone } })
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data)
          }
        })
        .catch(err => {
          console.error('获取用户信息失败:', err)
        })
    }
  }, [phone])

  return (
    <div className="layout">
      {/* 侧边栏 */}
      <aside className="sider">
        <div className="avatar">
          <div className="circle">助</div>
          <div className="label">助教: {userInfo ? userInfo.username : '...'}</div>
          <div className="label">用户Id: {userInfo ? userInfo.userId : '...'}</div>
          <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/assistant/assistant-user">用户管理</Link></li>
          <li><Link to="/assistant/assistant-course">课程管理</Link></li>
          <li><Link to="/assistant/assistant-task">课后任务管理</Link></li>
          <li><Link to="/assistant/assistant-exam">模拟考试管理</Link></li>
          <li><Link to="/assistant/assistant-study-room" className="active">自习室管理</Link></li>
          <li><Link to="/assistant/assistant-resource">资源管理</Link></li>
          <li><Link to="/assistant/vanalytics">学习分析</Link></li>
          <li><Link to="/" onClick={() => localStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      {/* 主内容 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，助教 {userInfo ? userInfo.username : '...'}！目前自习室模块暂不需要助教操作。</h2>
        </header>

        <section className="content">
          <div className="card-grid">
            <div className="card disabled-card">
              <h3>暂无功能</h3>
              <p>自习室管理模块暂不开放助教权限。</p>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AssistantStudyRoom