import React from 'react'
import { Link } from 'react-router-dom'
import './teacher.css'

const TeacherUser = () => {
    const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

    return (
        <div className="teacher-layout">
            <aside className="sider">
                <div className="avatar">
                    <div className="circle">教</div>
                    <div className="label">教师: {storedUser.username || '...'}</div>
                    <div className="label">用户ID: {storedUser.userId || '...'}</div>
                    <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
                </div>
                <ul className="menu">
                    <li className="active"><Link to="/teacher/teacher-user">个人中心</Link></li>
                    <li><Link to="/teacher/teacher-course">课程管理</Link></li>
                    <li><Link to="/teacher/teacher-task">课后任务管理</Link></li>
                    <li><Link to="/teacher/teacher-exam">模拟考试管理</Link></li>
                    <li><Link to="/teacher/teacher-study-room">自习室管理</Link></li>
                    <li><Link to="/teacher/teacher-resource">资源管理</Link></li>
                    <li><Link to="/teacher/teacher-analytics">学习分析</Link></li>
                    <li><Link to="/teacher/teacher-notification">通知中心</Link></li>
                    <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，{storedUser.username || '教师'}！请选择下方卡片进入相关功能。</h2>
                </header>

                <section className="content">
                    <div className="card-grid">
                        <div className="card">
                            <h3>修改个人信息</h3>
                            <p>更新您的姓名、联系方式等个人资料。</p>
                            <Link to="/teacher/teacher-update-info">立即修改</Link>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default TeacherUser