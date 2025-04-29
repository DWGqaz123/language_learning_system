// LoginPage.jsx
import React, { useState } from 'react'
import axios from 'axios'

const LoginPage = () => {
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleLogin = async () => {
    setError('')
    setLoading(true)

    try {
      const res = await axios.post('/api/users/login', {
        phoneNumber: phone,
        password,
      })

      const response = res.data

      if (response.code === 200) {
        const { userId, username, phoneNumber, roleName, accountStatus } = response.data

        if (!accountStatus) {
          sessionStorage.setItem('pendingPhone', phoneNumber)
          sessionStorage.setItem('pendingUserId', userId)
          window.location.href = '/activate-account'
          return
        }

        sessionStorage.setItem('phone', phoneNumber)
        sessionStorage.setItem('username', username)
        sessionStorage.setItem('userId', userId)
        sessionStorage.setItem('userInfo', JSON.stringify(response.data))
        sessionStorage.setItem('roleName', roleName)

        switch (roleName) {
          case 'student':
            window.location.href = '/student/student-user'
            break
          case 'teacher':
            window.location.href = '/teacher/teacher-user'
            break
          case 'assistant':
            window.location.href = '/assistant/assistant-user'
            break
          case 'admin':
            window.location.href = '/admin/admin-user'
            break
          default:
            alert('未知角色，无法跳转')
        }
      } else {
        setError(response.message || '登录失败')
      }
    } catch (err) {
      setError(err.response?.data?.message || '请求失败，请检查服务器连接')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>登录语言学习机构系统</h2>
        <input
          type="text"
          placeholder="请输入账号（手机号）"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          style={styles.input}
        />
        <input
          type="password"
          placeholder="请输入密码"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          style={styles.input}
        />
        <button onClick={handleLogin} disabled={loading} style={styles.button}>
          {loading ? '登录中...' : '登录'}
        </button>
        {error && <p style={styles.error}>{error}</p>}
      </div>
    </div>
  )
}

const styles = {
  page: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    width: '100%',
    background: 'linear-gradient(to right, #6dd5ed, #2193b0)',
    padding: '20px', // 避免小屏幕内容贴边
    boxSizing: 'border-box',
  },
  card: {
    width: '100%',
    maxWidth: '400px',
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '40px 30px',
    boxShadow: '0 8px 20px rgba(0, 0, 0, 0.2)',
    boxSizing: 'border-box',
  },
  title: {
    marginBottom: '30px',
    textAlign: 'center',
    fontSize: '24px',
    fontWeight: 'bold',
    color: '#333',
  },
  input: {
    width: '100%',
    padding: '12px 16px',
    marginBottom: '15px',
    border: '1px solid #ccc',
    borderRadius: '8px',
    fontSize: '16px',
    boxSizing: 'border-box',
  },
  button: {
    width: '100%',
    padding: '12px 16px',
    backgroundColor: '#2193b0',
    color: '#fff',
    fontSize: '16px',
    fontWeight: 'bold',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    transition: 'background-color 0.3s',
  },
  error: {
    marginTop: '15px',
    color: 'red',
    fontSize: '14px',
    textAlign: 'center',
  },
}

export default LoginPage