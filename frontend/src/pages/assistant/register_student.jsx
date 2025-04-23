import React, { useState } from 'react'
import axios from 'axios'
import './register_student.css'
import { useNavigate } from 'react-router-dom'

const RegisterStudent = () => {
    const [username, setUsername] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [description, setDescription] = useState('')
    const [lessonHours, setLessonHours] = useState('')
    const [message, setMessage] = useState('')
    const [error, setError] = useState('')
    const [createdUserId, setCreatedUserId] = useState(null)
    const navigate = useNavigate()

    const handleSubmit = async () => {
        setMessage('')
        setError('')

        if (!username || !phoneNumber || !lessonHours) {
            setError('请填写所有必填项')
            return
        }

        try {
            const res = await axios.post('/api/users/register-student', {
                username,
                phoneNumber,
                description,
                lessonHours: Number(lessonHours),
                password: '123456' // ✅ 统一设定
            })

            if (res.data.code === 200) {
                setMessage('学员录入成功！初始密码为123456')
                setCreatedUserId(res.data.data?.userId || null)  // 获取 userId
                setUsername('')
                setPhoneNumber('')
                setDescription('')
                setLessonHours('')
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
                <button className="back-button" onClick={() => navigate('/assistant-user')}>← 返回</button>
                <h2>录入学员信息</h2>
                <input
                    type="text"
                    placeholder="学员姓名 *"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="手机号 *"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                />
                <input
                    type="number"
                    placeholder="1对1课时数 *"
                    value={lessonHours}
                    onChange={(e) => setLessonHours(e.target.value)}
                />
                <textarea
                    placeholder="描述（选填）"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
                <button onClick={handleSubmit}>提交录入</button>
                {message && <p className="success">{message}</p>}
                {createdUserId && (
                    <p className="success">该学员的用户ID为：{createdUserId}</p>
                )}
                {error && <p className="error">{error}</p>}
            </div>
        </div>
    )
}

export default RegisterStudent