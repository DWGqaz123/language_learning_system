import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './review_update_request.css'
import { useNavigate } from 'react-router-dom'

const ReviewUpdatePage = () => {
    const [requests, setRequests] = useState([])
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')
    const navigate = useNavigate()

    useEffect(() => {
        axios.get('/api/users/pending-update-requests')
            .then(res => {
                if (res.data.code === 200) {
                    setRequests(res.data.data)
                }
            })
            .catch(err => {
                setError('获取待审核数据失败')
            })
    }, [])

    const handleReview = async (userId, approved, updateData) => {
        try {
            const payload = {
                userId,
                username: updateData.username,
                phoneNumber: updateData.phoneNumber,
                description: updateData.description,
                password: updateData.password || null,
                approved
            }

            await axios.put('/api/users/review-update', payload)

            setSuccess(`用户 ${userId} 的申请已${approved ? '通过' : '驳回'}`)
            // 刷新页面数据
            setRequests(prev => prev.filter(req => req.userId !== userId))
        } catch (err) {
            setError(err.response?.data?.message || '审核失败')
        }
    }

    return (
        <div className="review-wrapper">
            <button className="back-button" onClick={() => navigate('/admin-user')}>← 返回</button>
            <h2>用户信息修改审核</h2>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">{success}</p>}

            {requests.length === 0 && <p>暂无待审核请求</p>}

            {requests.map((req, index) => {
                let updates = {}
                try {
                    updates = JSON.parse(req.pendingUpdateJson)
                } catch { }

                return (
                    <div key={index} className="review-card">
                        <h3>用户 ID: {req.userId}（{req.roleName}）</h3>
                        <p>当前姓名：{req.username}</p>
                        {updates.username && <p>修改后姓名：{updates.username}</p>}

                        <p>当前手机号：{req.phoneNumber}</p>
                        {updates.phoneNumber && <p>修改后手机号：{updates.phoneNumber}</p>}

                        {updates.description && <p>修改备注：{updates.description}</p>}

                        <div className="buttons">
                            <button onClick={() => handleReview(req.userId, true, updates)} className='agree'>通过</button>
                            <button onClick={() => handleReview(req.userId, false, updates)} className="reject">驳回</button>
                        </div>
                    </div>
                )
            })}
        </div>
    )
}

export default ReviewUpdatePage