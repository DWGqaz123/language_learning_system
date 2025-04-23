import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './update_info.css'

const UpdateInfo = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(localStorage.getItem('userInfo') || '{}')

  const [username, setUsername] = useState(storedUser.username || '')
  const [phoneNumber, setPhoneNumber] = useState(storedUser.phoneNumber || '')
  const [description, setDescription] = useState(storedUser.description || '')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async () => {
    setMessage('')
    setError('')

    if (!username || !phoneNumber || !password) {
      setError('请填写所有必填项')
      return
    }

    try {
      const res = await axios.post('/api/users/update-request', {
        userId: storedUser.userId,
        username,
        phoneNumber,
        description,
        password,
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
        <button className="back-button" onClick={() => navigate('/assistant-user')}>← 返回</button>
        <h2>修改个人信息</h2>
        <p className="gray">用户ID：{storedUser.userId}</p>

        <input type="text" value={storedUser.userId} disabled className="readonly" />
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
        <input
          type="password"
          placeholder="新密码 *"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <textarea
          placeholder="描述（选填）"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button className="submit-button" onClick={handleSubmit}>提交申请</button>

        {message && <p className="success">{message}</p>}
        {error && <p className="error">{error}</p>}
      </div>
    </div>
  )
}

export default UpdateInfo