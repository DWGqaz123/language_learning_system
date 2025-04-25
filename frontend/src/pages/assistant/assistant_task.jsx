import React, { useEffect, useState } from 'react'
import './assistant.css'
import { Link } from 'react-router-dom'
import axios from 'axios'

const AssistantTask = () => {
    const phone = localStorage.getItem('phone') || ''
    const [userInfo, setUserInfo] = useState(null)

    useEffect(() => {
        if (phone) {
            axios.get(`/api/users/phone`, { params: { phoneNumber: phone } })
                .then(res => {
                    if (res.data.code === 200) {
                        setUserInfo(res.data.data)
                    }
                })
                .catch(err => {
                    console.error('获取用户信息失败:', err)
                })
        }
    }, [phone])

    return (
        <div className="layout">
            <aside className="sider">
                <div className="avatar">
                    <div className="circle">助</div>
                    <div className="label">助教: {userInfo ? userInfo.username : '...'}</div>
                    <div className="label">用户Id: {userInfo ? userInfo.userId : '...'}</div>
                    <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
                </div>
                <ul className="menu">
                    <li><Link to="/assistant/assistant-user">用户管理</Link></li>
                    <li><Link to="/assistant/assistant-course">课程管理</Link></li>
                    <li className="active"><Link to="/assistant/assistant-task">课后任务管理</Link></li>
                    <li><Link to="/assistant/assistant-exam">模拟考试管理</Link></li>
                    <li><Link to="/assistant/assistant-study-room">自习室管理</Link></li>
                    <li><Link to="/assistant/assistant-resource">资源管理</Link></li>
                    <li><Link to="/assistant/assistant-analytics">学习分析</Link></li>
                    <li><Link to="/" onClick={() => localStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，助教 {userInfo ? userInfo.username : '...'}！请选择下方卡片操作课后任务功能。</h2>
                </header>

                <section className="content">
                    <div className="card-grid">
                        <div className="card">
                            <h3>发布任务</h3>
                            <p>布置课后作业或训练任务，通知相关学员。</p>
                            <Link to="/assistant/task-publish">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>已发布任务详情</h3>
                            <p>查看所有已发布任务和学员的任务完成情况。</p>
                            <Link to="/assistant/task-detail">立即进入</Link>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default AssistantTask