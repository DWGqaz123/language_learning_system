import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import './teacher.css'

const TeacherCourse = () => {
    const phone = sessionStorage.getItem('phone') || ''
    const [userInfo, setUserInfo] = useState(null)
    const [weeklySchedule, setWeeklySchedule] = useState({})

    useEffect(() => {
        if (phone) {
            axios.get('/api/users/phone', { params: { phoneNumber: phone } })
                .then(res => {
                    if (res.data.code === 200) {
                        setUserInfo(res.data.data)
                        fetchWeeklySchedule(res.data.data.userId)
                    }
                })
                .catch(err => {
                    console.error('获取用户信息失败:', err)
                })
        }
    }, [phone])

    const fetchWeeklySchedule = async (userId) => {
        try {
            const res = await axios.get('/api/courses/my-schedules', { params: { userId } })
            const schedules = res.data.data || []

            const startDate = new Date()
            const weekData = {}

            for (let i = 0; i < 7; i++) {
                const date = new Date()
                date.setDate(startDate.getDate() + i)
                const dateKey = date.toISOString().split('T')[0]
                weekData[dateKey] = {
                    morning: [],
                    afternoon: [],
                    evening: []
                }
            }

            schedules.forEach(schedule => {
                const classTime = new Date(schedule.classTime)
                const classDate = classTime.toISOString().split('T')[0]
                const classHour = classTime.getHours()

                if (weekData[classDate]) {
                    if (classHour >= 5 && classHour < 12) {
                        weekData[classDate].morning.push(schedule)
                    } else if (classHour >= 12 && classHour < 18) {
                        weekData[classDate].afternoon.push(schedule)
                    } else {
                        weekData[classDate].evening.push(schedule)
                    }
                }
            })

            setWeeklySchedule(weekData)
        } catch (err) {
            console.error('获取本周课表失败:', err)
        }
    }

    return (
        <div className="teacher-layout">
            {/* 左侧导航栏 */}
            <aside className="sider">
                <div className="avatar">
                    <div className="circle">教</div>
                    <div className="label">教师: {userInfo ? userInfo.username : '...'}</div>
                    <div className="label">用户ID: {userInfo ? userInfo.userId : '...'}</div>
                    <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
                </div>
                <ul className="menu">
                    <li><Link to="/teacher/teacher-user">个人中心</Link></li>
                    <li className="active"><Link to="/teacher/teacher-course">课程管理</Link></li>
                    <li><Link to="/teacher/teacher-task">课后任务管理</Link></li>
                    <li><Link to="/teacher/teacher-exam">模拟考试管理</Link></li>
                    <li><Link to="/teacher/teacher-study-room">自习室管理</Link></li>
                    <li><Link to="/teacher/teacher-resource">资源管理</Link></li>
                    <li><Link to="/teacher/teacher-analytics">学习分析</Link></li>
                    <li><Link to="/teacher/teacher-notification">通知中心</Link></li>
                    <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            {/* 主内容区 */}
            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，教师 {userInfo ? userInfo.username : '...'}！请选择功能开始管理吧。</h2>
                </header>

                <section className="content">
                    {/* 功能卡片 */}
                    <div className="card-grid">
                        <div className="card">
                            <h3>查看个人课程</h3>
                            <p>浏览您教授的所有课程。</p>
                            <Link to="/teacher/teacher-course-list">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>查看个人课表</h3>
                            <p>查看您未来的排课安排。</p>
                            <Link to="/teacher/teacher-schedule-manage">立即进入</Link>
                        </div>
                        <div className="card">
                            <h3>学员课堂反馈</h3>
                            <p>查看并记录学员的课堂表现。</p>
                            <Link to="/teacher/teacher-feedback">立即进入</Link>
                        </div>
                    </div>

                    {/* 本周课表展示 */}
                    <div className="teacher-schedule-section">
                        <h3>本周课表</h3>
                        <div className="calendar-table">
                            <div className="calendar-header">
                                <div>日期</div>
                                <div>上午</div>
                                <div>下午</div>
                                <div>晚上</div>
                            </div>
                            {Object.keys(weeklySchedule).map(date => (
                                <div key={date} className="calendar-row">
                                    <div className="calendar-date">{date}</div>
                                    <div className="calendar-cell">
                                        {weeklySchedule[date].morning.length > 0 ? weeklySchedule[date].morning.map(s => s.courseName).join('、') : '无'}
                                    </div>
                                    <div className="calendar-cell">
                                        {weeklySchedule[date].afternoon.length > 0 ? weeklySchedule[date].afternoon.map(s => s.courseName).join('、') : '无'}
                                    </div>
                                    <div className="calendar-cell">
                                        {weeklySchedule[date].evening.length > 0 ? weeklySchedule[date].evening.map(s => s.courseName).join('、') : '无'}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default TeacherCourse