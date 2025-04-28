import React, { useEffect, useState } from 'react'
import { PolarAngleAxis, RadialBarChart, RadialBar, Cell, Bar, BarChart, PieChart, Pie, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import './dash_board.css'

const DashBoard = () => {
    const [progressData, setProgressData] = useState(null)
    const [taskStats, setTaskStats] = useState(null)
    const [examStats, setExamStats] = useState(null)
    const [examTrend, setExamTrend] = useState([])

    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

    useEffect(() => {
        fetchProgress()
        fetchTaskStats()
        fetchExamStats()
        fetchExamTrend()
    }, [])

    const fetchProgress = async () => {
        try {
            const res = await fetch(`/api/analysis/course-progress?studentId=${userInfo.userId}`)
            const data = await res.json()
            setProgressData(data.data)
        } catch (error) {
            console.error('加载课程进度失败', error)
        }
    }

    const fetchTaskStats = async () => {
        try {
            const res = await fetch(`/api/analysis/task-statistics/${userInfo.userId}`)
            const data = await res.json()
            setTaskStats(data.data)
        } catch (error) {
            console.error('加载任务统计失败', error)
        }
    }

    const fetchExamStats = async () => {
        try {
            const res = await fetch(`/api/analysis/mock-exams/statistics/${userInfo.userId}`)
            const data = await res.json()
            setExamStats(data.data)
        } catch (error) {
            console.error('加载考试统计失败', error)
        }
    }

    const fetchExamTrend = async () => {
        try {
            const res = await fetch(`/api/analysis/mock-exams/score-trend/${userInfo.userId}`)
            const data = await res.json()
            setExamTrend(data.data || [])
        } catch (error) {
            console.error('加载考试趋势失败', error)
        }
    }

    return (
        <div className="dashboard">
            <div className="progress-card">
                <h3>当前学习进度</h3>
                <div className="progress-circle">
                    {progressData ? (
                        <ResponsiveContainer width="100%" height={200}>
                            <PieChart>
                                <Pie
                                    data={[
                                        { name: '已完成', value: progressData.completedPercentage },
                                        { name: '未完成', value: 100 - progressData.completedPercentage }
                                    ]}
                                    dataKey="value"
                                    cx="50%"
                                    cy="50%"
                                    innerRadius={60}
                                    outerRadius={80}
                                    startAngle={90}
                                    endAngle={-270}
                                >
                                    <Cell fill="#4CAF50" />    {/* 绿色：完成 */}
                                    <Cell fill="#ccc" />        {/* 灰色：未完成 */}
                                </Pie>
                                <Tooltip />
                            </PieChart>
                        </ResponsiveContainer>
                    ) : (
                        <p>加载中...</p>
                    )}
                </div>
                <p>{progressData ? `课程进度 ${progressData.totalHours - progressData.remainingHours} / ${progressData.totalHours} 课时` : '加载中...'}</p>
            </div>

            <div className="stats-card">
                <h3>任务完成情况</h3>

                {taskStats && (
                    <RadialBarChart
                        width={150}
                        height={150}
                        cx="50%"
                        cy="50%"
                        innerRadius="75%"
                        outerRadius="100%"
                        barSize={20}
                        startAngle={90}
                        endAngle={-270}
                        data={[{ name: '完成率', value: taskStats.completionRate }]}
                    >
                        <PolarAngleAxis
                            type="number"
                            domain={[0, 100]}
                            angleAxisId={0}
                            tick={false}
                        />
                        <RadialBar
                            background={{ fill: '#ccc' }}  // 背景圆，浅灰色
                            clockWise
                            dataKey="value"
                            fill="#23f"  // 进度颜色，绿色
                            cornerRadius={50}
                        />
                    </RadialBarChart>
                )}

                <div className="chart-label">
                    完成率: {taskStats ? `${taskStats.completionRate}%` : '加载中...'}
                </div>

                <div className="average-score">
                    平均得分: {taskStats ? `${taskStats.averageScore}分` : '加载中...'}
                </div>
            </div>

            <div className="exam-card">
                <h3>模拟考试</h3>
                <div className="trend-line">
                    {examTrend.length > 0 ? (
                        <ResponsiveContainer width="100%" height={200}>
                            <LineChart width={250} height={150} data={examTrend}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis />
                                <YAxis />
                                <Tooltip />
                                {/* 不添加 <Legend />，直接去掉 */}
                                <Line type="monotone" dataKey="score" stroke="#82ca9d" strokeWidth={2} />
                            </LineChart>
                        </ResponsiveContainer>
                    ) : (
                        <p>暂无考试数据</p>
                    )}
                </div>
                <p>
                    {examStats
                        ? `考试次数: ${examStats.examCount} | 平均成绩: ${examStats.averageScore}`
                        : '加载中...'}
                </p>
            </div>
        </div>
    )
}

export default DashBoard