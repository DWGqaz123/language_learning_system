import React, { useEffect, useState } from 'react'
import { PieChart, Pie, Cell, Tooltip, Legend } from 'recharts'
import './study_room_usage.css'

const COLORS = ['#6A5ACD', '#20B2AA', '#FF69B4']  

const StudyRoomUsage = () => {
  const [usageData, setUsageData] = useState(null)
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchUsageData()
  }, [])

  const fetchUsageData = async () => {
    try {
      const res = await fetch(`/api/study-rooms/usage-statistics/${userInfo.userId}`)
      const data = await res.json()
      setUsageData(data.data)
    } catch (error) {
      console.error('加载自习室使用数据失败', error)
    }
  }

  return (
    <div className="study-room-usage">
      <h3>自习室使用统计</h3>
      <div className="usage-chart-section">
        {usageData ? (
          <>
            <PieChart width={250} height={250}>
              <Pie
                data={[
                  { name: '上午', value: usageData.morningCount },
                  { name: '下午', value: usageData.afternoonCount },
                  { name: '晚上', value: usageData.eveningCount },
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
            <div className="usage-summary">
              <p><strong>总使用次数：</strong>{usageData.totalUsageCount}</p>
              <p><strong>上午：</strong>{usageData.morningCount} 次</p>
              <p><strong>下午：</strong>{usageData.afternoonCount} 次</p>
              <p><strong>晚上：</strong>{usageData.eveningCount} 次</p>
            </div>
          </>
        ) : (
          <p>加载使用数据中...</p>
        )}
      </div>
    </div>
  )
}

export default StudyRoomUsage