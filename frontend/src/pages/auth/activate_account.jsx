import React, { useState } from 'react'
import axios from 'axios'
import './activate.css'

const ActivateAccount = () => {
    const phoneNumber = localStorage.getItem('pendingPhone') || ''
    const userId = localStorage.getItem('pendingUserId') || ''
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    const handleActivate = async () => {
        setError('')
        setSuccess('')

        if (!password || !confirmPassword) {
            setError('请输入密码和确认密码')
            return
        }

        if (password !== confirmPassword) {
            setError('两次密码输入不一致')
            return
        }

        try {
            console.log('提交 userId 类型:', typeof userId, userId)
            const res = await axios.post('/api/users/activate-account', {
                userId: Number(userId),
                newPassword: password,
            })
            console.log('激活页传入 userId 类型:', typeof userId, userId)

            const response = res.data
            if (response.code === 200) {
                setSuccess('激活成功，请重新登录')
                // 清理激活缓存信息，2秒后跳转登录页
                localStorage.removeItem('pendingPhone')
                localStorage.removeItem('pendingUserId')
                setTimeout(() => {
                    window.location.href = '/login'
                }, 2000)
            } else {
                setError(response.message || '激活失败')
            }
        } catch (err) {
            setError(err.response?.data?.message || '激活失败，请稍后重试')
        }
    }

    return (
        <div className="activate-wrapper">
            <div className="activate-card">
                <h2>账户激活</h2>
                <p className="gray">手机号：{phoneNumber}</p>
                <p className="gray">用户ID：{userId}</p>
                <input
                    type="password"
                    placeholder="设置新密码"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="确认新密码"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                />
                <button onClick={handleActivate}>激活账户</button>
                {error && <p className="error">{error}</p>}
                {success && <p className="success">{success}</p>}
            </div>
        </div>
    )
}

export default ActivateAccount