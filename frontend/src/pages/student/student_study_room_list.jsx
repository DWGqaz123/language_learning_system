import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_study_room_list.css'

const StudentStudyRoomList = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [rooms, setRooms] = useState([])

  useEffect(() => {
    fetchRooms()
  }, [])

  const fetchRooms = async () => {
    try {
      const res = await axios.get('/api/study-rooms/reservations')
      setRooms(res.data.data || [])
    } catch (err) {
      alert('获取自习室列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleReserve = async (roomId, timeSlot) => {
    if (!window.confirm(`确认预约【${timeSlot}】时段吗？`)) return

    try {
      await axios.post('/api/study-rooms/reserve', {
        roomId,
        studentId: userId,
        reservationDate: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 明天
        timeSlot
      })
      alert('预约提交成功，等待审核')
      fetchRooms()
    } catch (err) {
      alert('预约失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="study-room-wrapper">
      <button className="back-button" onClick={() => navigate('/student/student-study-room')}>← 返回</button>
      <h2 className="title">明日自习室预约</h2>

      <div className="room-grid">
        {rooms.map(room => (
          <div key={room.roomId} className="room-card">
            <h3>{room.roomName}</h3>
            <p>位置：{room.location}</p>

            <div className="time-slots">
              {['上午', '下午', '晚上'].map(slot => (
                <button
                  key={slot}
                  className={`time-slot-button ${room.timeSlotStatus[slot] !== '可预约' ? 'disabled' : ''}`}
                  onClick={() => handleReserve(room.roomId, slot)}
                  disabled={room.timeSlotStatus[slot] !== '可预约'}
                >
                  {slot} - {room.timeSlotStatus[slot]}
                </button>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default StudentStudyRoomList