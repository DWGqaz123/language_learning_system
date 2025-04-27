import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import './admin.css'

const AdminStudyRoomManage = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  const [roomId, setRoomId] = useState('')
  const [date, setDate] = useState('')
  const [usageStats, setUsageStats] = useState([])

  const fetchUsageStats = async () => {
    if (!roomId || !date) {
      alert('请填写完整查询条件')
      return
    }
    try {
      const res = await axios.get('/api/study-rooms/usage-stats', {
        params: { roomId, date }
      })
      setUsageStats(res.data.data || [])
    } catch (err) {
      alert('获取使用统计失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="layout">
      <aside className="sider">
        <div className="avatar">
          <div className="circle">管</div>
          <div className="label">管理员: {storedUser.username || '...'}</div>
          <div className="label">用户ID: {storedUser.userId || '...'}</div>
          <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/admin/admin-user">用户管理</Link></li>
          <li className="active"><Link to="/admin/admin-study-room">自习室管理</Link></li>
          <li><Link to="/admin/admin-resource">资源管理</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，{storedUser.username || '管理员'}！这是自习室管理模块。</h2>
        </header>

        <section className="content">
          {/* 管理功能区 */}
          <div className="card-grid">
            <div className="card">
              <h3>自习室列表管理</h3>
              <p>查看、修改、删除自习室信息，管理自习室状态。</p>
               <Link to="/admin/admin-study-room-list">立即进入</Link>
            </div>

            <div className="card">
              <h3>自习室预约审核</h3>
              <p>审核学生提交的自习室预约申请，确保自习安排合理。</p>
               <Link to="/admin/admin-study-room-review">立即进入</Link>
            </div>
          </div>

          {/* 使用统计区 */}
          <div className="admin-studyroom-usage-card">
            <h3>自习室使用统计</h3>
            <div className="usage-form">
              <input
                type="number"
                placeholder="输入自习室ID"
                value={roomId}
                onChange={(e) => setRoomId(e.target.value)}
              />
              <input
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
              />
              <button className="small-button" onClick={fetchUsageStats}>
                查询
              </button>
            </div>
            <table className="usage-stats-table">
              <thead>
                <tr>
                  <th>时段</th>
                  <th>预约人数</th>
                  <th>签到人数</th>
                  <th>签退人数</th>
                </tr>
              </thead>
              <tbody>
                {usageStats.length > 0 ? (
                  usageStats.map((stat, index) => (
                    <tr key={index}>
                      <td>{stat.timeSlot}</td>
                      <td>{stat.totalReservations}</td>
                      <td>{stat.signedInCount}</td>
                      <td>{stat.signedOutCount}</td>
                    </tr>
                  ))
                ) : (
                  <tr><td colSpan="4" style={{ textAlign: 'center' }}>暂无数据</td></tr>
                )}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AdminStudyRoomManage