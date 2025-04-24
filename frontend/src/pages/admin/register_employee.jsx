import React, { useState } from 'react'
import './register_employee.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const RegisterEmployee = () => {
  const [username, setUsername] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [description, setDescription] = useState('')
  const [roleId, setRoleId] = useState('') // 2 为教师，3 为助教
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async () => {
    setMessage('')
    setError('')

    if (!username || !phoneNumber || !roleId) {
      setError('请填写所有必填项')
      return
    }

    try {
      const res = await axios.post('/api/users/import-employee', {
        username,
        phoneNumber,
        description,
        roleId: Number(roleId)
      })

      if (res.data.code === 200) {
        setMessage('员工录入成功！初始密码为123456，账号状态为未激活')
        setUsername('')
        setPhoneNumber('')
        setDescription('')
        setRoleId('')
      } else {
        setError(res.data.message || '录入失败')
      }
    } catch (err) {
      setError(err.response?.data?.message || '请求失败')
    }
  }

  return (
    <div className="register-wrapper">
      <div className="register-card">
        <button className="back-button" onClick={() => navigate('/admin/admin-user')}>← 返回</button>
        <h2>录入员工信息</h2>
        <input
          type="text"
          placeholder="姓名 *"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="text"
          placeholder="手机号 *"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
        />
        <select value={roleId} onChange={(e) => setRoleId(e.target.value)}>
          <option value="">请选择角色 *</option>
          <option value="2">教师</option>
          <option value="3">助教</option>
        </select>
        <textarea
          placeholder="描述（选填）"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <button onClick={handleSubmit}>提交录入</button>
        {message && <p className="success">{message}</p>}
        {error && <p className="error">{error}</p>}
      </div>
    </div>
  )
}

export default RegisterEmployee