import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './admin.css'
import { Link } from 'react-router-dom'

const AdminUser = () => {
  const phone = sessionStorage.getItem('phone') || ''
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
          <div className="circle">管</div>
          <div className="label">管理员: {userInfo ? userInfo.username : '...'}</div>
          <div className="label">用户Id: {userInfo ? userInfo.userId : '...'}</div>
          <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
        </div>
        <ul className="menu">
          <li className="active">用户管理</li>
          <li>自习室管理</li>
          <li>资源管理</li>
          <li>退出系统</li>
        </ul>
      </aside>

      {/* 主内容 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>
            欢迎您，管理员 {userInfo ? userInfo.username : '...'}！请选择下方的卡片或左边的模块开始管理吧！
          </h2>
        </header>

        <section className="content">
          <div className="card-grid">
            <div className="card">
              <h3>审核用户信息修改</h3>
              <p>审批用户提交的修改申请，确保信息准确。</p>
              <Link to="/admin/review-update-request">立即进入</Link>
            </div>
            <div className="card">
              <h3>录入员工</h3>
              <p>为系统添加教师与助教账户。</p>
              <Link to="/admin/register-employee">立即进入</Link>
            </div>
            <div className="card">
              <h3>用户列表查看</h3>
              <p>查看所有系统用户</p>
              <Link to="/admin/role-management">立即进入</Link>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AdminUser