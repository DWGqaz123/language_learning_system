import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './assistant.css'
import { Link } from 'react-router-dom'

const AssistantUser = () => {
  const phone = localStorage.getItem('phone') || ''
  const [userInfo, setUserInfo] = useState(null)
  console.log('读取手机号用于查询用户:', phone)
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
            <li><Link to="/assistant/assistant-user" className="active">用户管理</Link></li>
            <li><Link to="/assistant/assistant-course" >课程管理</Link></li>
            <li><Link to="/assistant/task">课后任务管理</Link></li>
            <li><Link to="/assistant/mock-exam">模拟考试管理</Link></li>
            <li><Link to="/assistant/study-room">自习室管理</Link></li>
            <li><Link to="/assistant/resource">资源管理</Link></li>
            <li><Link to="/assistant/analytics">学习分析</Link></li>
            <li><Link to="/" onClick={() => localStorage.clear()}>退出系统</Link></li>
          </ul>
      </aside>

      {/* 主内容 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>
            欢迎您，助教 {userInfo ? userInfo.username : '...'}！请选择下方的卡片或左边的模块开始工作吧！
          </h2>
        </header>

        <section className="content">
          <div className="card-grid">
            <div className="card">
              <h3>录入学员</h3>
              <p>添加新学员信息，生成账户用于后续排课与管理。</p>
              <Link to="/assistant/register-student">立即进入</Link>
            </div>
            <div className="card">
              <h3>修改个人信息</h3>
              <p>修改您的姓名、手机号、描述或密码。</p>
              <Link to="/assistant/update-info">修改个人信息</Link>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AssistantUser
