import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { Link } from 'react-router-dom'
import './student.css'

const StudentUser = () => {
    const phone = sessionStorage.getItem('phone') || ''
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
        <div className="student-layout">
            {/* 侧边栏 */}
            <aside className="sider">
                <div className="avatar">
                    <div className="circle">学</div>
                    <div className="label">学员: {userInfo ? userInfo.username : '...'}</div>
                    <div className="label">用户Id: {userInfo ? userInfo.userId : '...'}</div>
                    <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
                </div>
                <ul className="menu">
                    <li className="active"><Link to="/student/student-user">个人中心</Link></li>
                    <li><Link to="/student/student-course">课程管理</Link></li>
                    <li><Link to="/student/student-task">课后任务管理</Link></li>
                    <li><Link to="/student/student-exam">模拟考试管理</Link></li>
                    <li><Link to="/student/student-study-room">自习室管理</Link></li>
                    <li><Link to="/student/student-resource">资源管理</Link></li>
                    <li><Link to="/student/student-analytics">学习分析</Link></li>
                    <li><Link to="/student/student-notification">通知中心</Link></li>
                    <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            {/* 主内容 */}
            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，学员 {userInfo ? userInfo.username : '...'}！请选择下方卡片操作您的个人功能。</h2>
                </header>

                <section className="content">
                    <div className="card-grid">
                        <div className="card">
                            <h3>修改个人信息</h3>
                            <p>修改您的姓名、手机号或密码。</p>
                            <Link to="/student/update-info">修改个人信息</Link>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default StudentUser