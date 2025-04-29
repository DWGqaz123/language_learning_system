import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import '../assistant/update_info.css'

const TeacherUpdateInfo = () => {
  const navigate = useNavigate()
  const phone = sessionStorage.getItem('phone') || ''

  const [userInfo, setUserInfo] = useState(null)
  const [username, setUsername] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [description, setDescription] = useState('')
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
        setDescription(user.description || '')
        setCurrentPassword(user.password || '')
      }
    } catch (err) {
      console.error('获取教师信息失败:', err)
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
        description,
        password: newPassword || undefined // 不填写新密码则不提交password字段
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
        <button className="back-button" onClick={() => navigate('/teacher/teacher-user')}>← 返回</button>
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
            <div className="password-wrapper">
              <input
                type={showCurrentPassword ? 'text' : 'password'}
                value={currentPassword}
                className="readonly"
                readOnly
                placeholder="当前密码"
              />
              <span className="toggle-password" onClick={() => setShowCurrentPassword(!showCurrentPassword)}>
                {showCurrentPassword ? '🙈' : '👁️'}
              </span>
            </div>

            {/* 新密码框 */}
            <div className="password-wrapper">
              <input
                type={showNewPassword ? 'text' : 'password'}
                placeholder="新密码（可留空）"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
              <span className="toggle-password" onClick={() => setShowNewPassword(!showNewPassword)}>
                {showNewPassword ? '🙈' : '👁️'}
              </span>
            </div>

            {/* 描述输入 */}
            <textarea
              placeholder="描述（选填）"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />

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

export default TeacherUpdateInfo