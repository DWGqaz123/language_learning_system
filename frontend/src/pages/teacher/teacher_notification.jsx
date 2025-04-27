import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import './teacher_notification.css'

const TeacherNotificationManage = () => {
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId
  const [notifications, setNotifications] = useState([])
  const [selectedNotification, setSelectedNotification] = useState(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    fetchNotifications()
  }, [])

  const fetchNotifications = async () => {
    try {
      const res = await axios.post('/api/notifications/query', {
        receiverId: userId,
        page: 0,
        size: 100
      })
      const result = res.data
      if (Array.isArray(result)) {
        setNotifications(result)
      } else {
        setNotifications([])
      }
    } catch (err) {
      alert('获取通知失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleMarkAsRead = async (notificationId) => {
    try {
      await axios.post('/api/notifications/mark-as-read', { notificationId })
      alert('已接收通知')
      fetchNotifications()
    } catch (err) {
      alert('接收失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return ''
    return dateTimeStr.replace('T', ' ').slice(0, 16)
  }

  const openDetailModal = (notification) => {
    setSelectedNotification(notification)
    setShowModal(true)
  }

  const closeModal = () => {
    setShowModal(false)
    setSelectedNotification(null)
  }

  return (
    <div className="teacher-layout">
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
          <li><Link to="/teacher/teacher-analytics">学习分析</Link></li>
          <li className="active"><Link to="/teacher/teacher-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，教师 {storedUser.username || '...'}！这是您的通知中心。</h2>
        </header>

        <section className="teacher-notification-wrapper">
          <div className="notification-list">
            {notifications.length > 0 ? (
              notifications.map(notif => (
                <div key={notif.notificationId} className={`notification-card ${notif.status === '已读' ? 'read' : 'unread'}`}>
                  <div className="notif-top">
                    <span className="notif-type">{notif.notificationType || '通知'}</span>
                    <span className="notif-time">{formatDateTime(notif.sentTime)}</span>
                  </div>
                  <div className="notif-content">{notif.content.length > 30 ? notif.content.slice(0, 30) + '...' : notif.content}</div>
                  <div className="notif-actions">
                    <button className="small-button" onClick={() => openDetailModal(notif)}>查看详情</button>
                    {notif.status !== '已读' && (
                      <button className="small-button" onClick={() => handleMarkAsRead(notif.notificationId)}>接收通知</button>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <p className="no-notification">暂无通知</p>
            )}
          </div>
        </section>
      </main>

      {showModal && selectedNotification && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>通知详情</h3>
            <p><strong>通知类型：</strong>{selectedNotification.notificationType}</p>
            <p><strong>发送时间：</strong>{formatDateTime(selectedNotification.sentTime)}</p>
            <p><strong>内容：</strong>{selectedNotification.content}</p>
            <div className="modal-buttons">
              <button onClick={closeModal}>关闭</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TeacherNotificationManage