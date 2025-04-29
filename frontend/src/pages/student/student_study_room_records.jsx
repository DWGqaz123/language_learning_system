// StudentStudyRoomRecords.jsx
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_study_room_records.css'

const StudentStudyRoomRecords = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [records, setRecords] = useState([])

  useEffect(() => {
    fetchRecords()
  }, [])

  const fetchRecords = async () => {
    try {
      const res = await axios.get(`/api/study-rooms/my-reservations?studentId=${userId}`)
      setRecords(res.data.data || [])
    } catch (err) {
      alert('获取预约记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleCancel = async (record) => {
    if (!window.confirm('确认取消该预约？')) return

    try {
      await axios.delete('/api/study-rooms/cancel-reservation', {
        params: {
          roomId: record.roomId,
          studentId: userId,
          reservationDate: record.reservationDate,
          timeSlot: record.timeSlot
        }
      })
      await fetchRecords()
      alert('取消预约成功')
    } catch (err) {
      alert('取消失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleSign = async (record, action) => {
    const confirmText = action === 'signIn' ? '确认签到？' : '确认签退？'
    if (!window.confirm(confirmText)) return

    try {
      await axios.post('/api/study-rooms/sign', {
        roomId: record.roomId,
        studentId: userId,
        reservationDate: record.reservationDate,
        timeSlot: record.timeSlot,
        action: action,
        timestamp: new Date().toISOString()  // ⭐️ 加上当前时间戳，后端需要
      })

      await fetchRecords()  // ⭐️ 签到/签退后立即刷新页面数据
      alert(action === 'signIn' ? '签到成功' : '签退成功')
    } catch (err) {
      alert((action === 'signIn' ? '签到失败：' : '签退失败：') + (err.response?.data?.message || '未知错误'))
    }
  }

  const canSignIn = (record) => {
    return record.reviewStatus === '通过' && (!record.signInTime || record.signInTime.trim() === '')
  }

  const canSignOut = (record) => {
    return record.reviewStatus === '通过' && record.signInTime && (!record.signOutTime || record.signOutTime.trim() === '')
  }

  return (
    <div className="records-wrapper">
      <button className="back-button" onClick={() => navigate('/student/student-study-room')}>← 返回</button>
      <h2 className="title">个人预约记录</h2>

      <div className="records-grid">
        {records.map((record, index) => (
          <div key={index} className="record-card">
            <h3>{record.roomName}</h3>
            <p>位置：{record.location}</p>
            <p>预约日期：{record.reservationDate}</p>
            <p>时段：{record.timeSlot}</p>
            <p>审核状态：{record.reviewStatus}</p>

            <div className="button-group">
              {record.reviewStatus === '待审核' && (
                <button className="cancel-btn" onClick={() => handleCancel(record)}>取消预约</button>
              )}
              {canSignIn(record) && (
                <button className="sign-btn" onClick={() => handleSign(record, 'signIn')}>签到</button>
              )}
              {canSignOut(record) && (
                <button className="sign-btn" onClick={() => handleSign(record, 'signOut')}>签退</button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default StudentStudyRoomRecords