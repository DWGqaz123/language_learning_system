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
        fetchPendingRequests()
    }, [])

    const fetchPendingRequests = async () => {
        try {
            const res = await axios.get('/api/users/pending-update-requests')
            if (res.data.code === 200) {
                setRequests(res.data.data)
            }
        } catch (err) {
            setError('获取待审核数据失败')
        }
    }

    const handleReview = async (userId, approved, updateData, originalData) => {
        try {
            const payload = {
                userId,
                username: updateData.username || originalData.username,
                phoneNumber: updateData.phoneNumber || originalData.phoneNumber,
                description: updateData.description || originalData.description,
                password: updateData.password !== undefined
                    ? (updateData.password || originalData.password)
                    : originalData.password,
                approved
            }

            await axios.put('/api/users/review-update', payload)

            setSuccess(`用户 ${userId} 的申请已${approved ? '通过' : '驳回'}`)
            setRequests(prev => prev.filter(req => req.userId !== userId))
        } catch (err) {
            setError(err.response?.data?.message || '审核失败')
        }
    }

    const displayPasswordStatus = (password) => {
        if (password !== undefined && password !== null && password !== '') {
            return '●●●●●（实际已存在）'
        }
        return '无密码'
    }

    return (
        <div className="review-wrapper">
            <button className="back-button" onClick={() => navigate('/admin/admin-user')}>← 返回</button>
            <h2>用户信息修改审核</h2>

            {error && <p className="error">{error}</p>}
            {success && <p className="success">{success}</p>}

            {requests.length === 0 && <p>暂无待审核请求</p>}

            {requests.map((req, index) => {
                let updates = {};
                try {
                    updates = JSON.parse(req.pendingUpdateJson);
                } catch { }

                return (
                    <div key={index} className="review-card">
                        <h3>用户 ID: {req.userId}（{req.roleName}）</h3>

                        <p>当前姓名：{req.username}</p>
                        {updates.username && <p>修改后姓名：{updates.username}</p>}

                        <p>当前手机号：{req.phoneNumber}</p>
                        {updates.phoneNumber && <p>修改后手机号：{updates.phoneNumber}</p>}

                        <p>当前备注：{req.description || '无'}</p>
                        {updates.description && <p>修改后备注：{updates.description}</p>}

                        {/* 🔥 显示当前密码 */}
                        <p>当前密码：{req.password ? '●●●●●（实际已存在）' : '无密码'}</p>

                        {/* 🔥 显示修改后密码 */}
                        {updates.password !== undefined && (
                            <p>修改后密码：{updates.password ? '●●●●●（新密码）' : '未填写新密码，沿用原密码'}</p>
                        )}

                        <div className="buttons">
                            <button
                                onClick={() => handleReview(req.userId, true, updates, req)}
                                className="agree"
                            >
                                通过
                            </button>
                            <button
                                onClick={() => handleReview(req.userId, false, updates, req)}
                                className="reject"
                            >
                                驳回
                            </button>
                        </div>
                    </div>
                );
            })}
        </div>
    )
}

export default ReviewUpdatePage