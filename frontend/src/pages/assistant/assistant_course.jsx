import React, { useEffect, useState } from 'react'
import './assistant.css'
import { Link } from 'react-router-dom'
import axios from 'axios'

const AssistantCourse = () => {
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
                    <li className="active"><Link to="/assistant/assistant-course">课程管理</Link></li>
                    <li><Link to="/assistant/task">课后任务管理</Link></li>
                    <li><Link to="/assistant/mock-exam">模拟考试管理</Link></li>
                    <li><Link to="/assistant/study-room">自习室管理</Link></li>
                    <li><Link to="/assistant/resource">资源管理</Link></li>
                    <li><Link to="/assistant/analytics">学习分析</Link></li>
                    <li><Link to="/" onClick={() => localStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，助教 {userInfo ? userInfo.username : '...'}！请选择下方卡片操作课程功能。</h2>
                </header>

                <section className="content">
                    <div className="card-grid">
                        <div className="card">
                            <h3>课程管理</h3>
                            <p>创建、编辑、删除课程信息。</p>
                            <Link to="/assistant/course-manage">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>排课管理</h3>
                            <p>管理课程的排课计划与时间安排。</p>
                            <Link to="/assistant/schedule-manage">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>班级管理</h3>
                            <p>管理学员班级成员与课程记录。</p>
                            <Link to="/assistant/class-manage">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>出勤与表现</h3>
                            <p>记录学生的出勤与课堂表现信息。</p>
                            <Link to="/assistant/attendance-manage">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>请假管理</h3>
                            <p>处理学生请假申请并进行审核。</p>
                            <Link to="/assistant/leave-manage">立即进入</Link>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default AssistantCourse