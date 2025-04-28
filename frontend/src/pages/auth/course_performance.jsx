import React, { useEffect, useState } from 'react'
import { PieChart, Pie, Cell, Tooltip, Legend } from 'recharts'
import './course_performance.css'

const COLORS = ['#4CAF50', '#FF5722', '#FFC107']  // 出勤、缺勤、请假颜色

const CoursePerformance = () => {
    const [attendanceData, setAttendanceData] = useState(null)
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

    useEffect(() => {
        fetchAttendanceData()
    }, [])

    const fetchAttendanceData = async () => {
        try {
            const res = await fetch(`/api/analysis/attendance-stats?studentId=${userInfo.userId}`)
            const data = await res.json()
            setAttendanceData(data.data)
        } catch (error) {
            console.error('加载出勤率数据失败', error)
        }
    }

    return (
        <div className="course-performance">

            <h3>课程出勤率统计</h3>
            <div className="attendance-section">

                {attendanceData ? (
                    <div className="attendance-chart">
                        <PieChart width={250} height={250}>
                            <Pie
                                data={[
                                    { name: '出勤', value: attendanceData.attendCount },
                                    { name: '缺勤', value: attendanceData.absentCount },
                                    { name: '请假', value: attendanceData.leaveCount },
                                ]}
                                cx="50%"
                                cy="50%"
                                innerRadius={60}
                                outerRadius={80}
                                paddingAngle={0}
                                dataKey="value"
                            >
                                {COLORS.map((color, index) => (
                                    <Cell key={index} fill={color} />
                                ))}
                            </Pie>
                            <Tooltip />
                            <Legend />
                        </PieChart>
                        <div className="attendance-summary">
                            <p><strong>总课次数：</strong>{attendanceData.totalRecords}</p>
                            <p><strong>出勤：</strong>{attendanceData.attendCount} 次</p>
                            <p><strong>缺勤：</strong>{attendanceData.absentCount} 次</p>
                            <p><strong>请假：</strong>{attendanceData.leaveCount} 次</p>
                        </div>
                    </div>
                ) : (
                    <p>加载出勤数据中...</p>
                )}
            </div>

        </div>
    )
}

export default CoursePerformance