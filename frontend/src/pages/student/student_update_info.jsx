import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import '../assistant/update_info.css'

const StudentUpdateInfo = () => {
  const navigate = useNavigate()
  const phone = sessionStorage.getItem('phone') || ''
  const [userInfo, setUserInfo] = useState(null)

  const [username, setUsername] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')

  const [showCurrentPassword, setShowCurrentPassword] = useState(false)
  const [showNewPassword, setShowNewPassword] = useState(false)

  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    if (phone) {
      fetchUserInfo()
    }
  }, [phone])

  const fetchUserInfo = async () => {
    try {
      const res = await axios.get('/api/users/phone', { params: { phoneNumber: phone } })
      if (res.data.code === 200) {
        const user = res.data.data
        setUserInfo(user)
        setUsername(user.username || '')
        setPhoneNumber(user.phoneNumber || '')
        setCurrentPassword(user.password || '')
      }
    } catch (err) {
      console.error('获取用户信息失败:', err)
    }
  }

  const handleSubmit = async () => {
    setMessage('')
    setError('')

    if (!username || !phoneNumber) {
      setError('请填写姓名和手机号')
      return
    }

    try {
      const res = await axios.post('/api/users/update-request', {
        userId: userInfo.userId,
        username,
        phoneNumber,
        password: newPassword || undefined // 新密码可选，不填则不提交密码字段
      })

      if (res.data.code === 200) {
        setMessage('修改申请已提交，请等待管理员审核')
      } else {
        setError(res.data.message || '提交失败')
      }
    } catch (err) {
      setError(err.response?.data?.message || '请求失败')
    }
  }

  return (
    <div className="update-wrapper">
      <div className="update-card">
        <button className="back-button" onClick={() => navigate('/student/student-user')}>← 返回</button>
        <h2>修改个人信息</h2>
        {userInfo ? (
          <>
            <p className="gray">用户ID：{userInfo.userId}</p>

            <input type="text" value={userInfo.userId} disabled className="readonly" />
            <input
              type="text"
              placeholder="新姓名 *"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
            <input
              type="text"
              placeholder="新手机号 *"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />

            {/* 当前密码框 */}
            <div className="password-field">
              <input
                type={showCurrentPassword ? 'text' : 'password'}
                value={currentPassword}
                readOnly
                className="readonly"
                placeholder="当前密码"
              />
              <span
                className="toggle-eye"
                onClick={() => setShowCurrentPassword(!showCurrentPassword)}
              >
                {showCurrentPassword ? '🙈' : '👁️'}
              </span>
            </div>

            {/* 新密码框 */}
            <div className="password-field">
              <input
                type={showNewPassword ? 'text' : 'password'}
                placeholder="新密码（可选）"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
              <span
                className="toggle-eye"
                onClick={() => setShowNewPassword(!showNewPassword)}
              >
                {showNewPassword ? '🙈' : '👁️'}
              </span>
            </div>

            <button className="submit-button" onClick={handleSubmit}>提交申请</button>

            {message && <p className="success">{message}</p>}
            {error && <p className="error">{error}</p>}
          </>
        ) : (
          <p>加载中...</p>
        )}
      </div>
    </div>
  )
}

export default StudentUpdateInfo