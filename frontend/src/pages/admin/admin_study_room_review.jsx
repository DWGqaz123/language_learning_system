import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './admin_study_room_review.css'
import dayjs from 'dayjs'

const AdminStudyRoomReservation = () => {
  const navigate = useNavigate()
  const [view, setView] = useState('pending') // pending or history
  const [pendingReservations, setPendingReservations] = useState([])
  const [historyReservations, setHistoryReservations] = useState([])
  const [selectedDate, setSelectedDate] = useState(dayjs().format('YYYY-MM-DD'))

  useEffect(() => {
    fetchPendingReservations()
    fetchHistoryReservations(selectedDate)
  }, [])

  const fetchPendingReservations = async () => {
    try {
      const res = await axios.get('/api/study-rooms/reservations/pending')
      if (Array.isArray(res.data?.data)) {
        setPendingReservations(res.data.data)
      } else {
        setPendingReservations([])
      }
    } catch (err) {
      alert('获取待审核预约失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const fetchHistoryReservations = async (date) => {
    try {
      const res = await axios.get('/api/study-rooms/reservations/all', {
        params: { date }
      })
      if (Array.isArray(res.data?.data)) {
        const passedRecords = res.data.data.filter(r => r.reviewStatus === '通过')
        setHistoryReservations(passedRecords)
      } else {
        setHistoryReservations([])
      }
    } catch (err) {
      alert('获取历史预约记录失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleReview = async (roomId, studentId, date, timeSlot, status) => {
    try {
      await axios.put('/api/study-rooms/review', {
        roomId,
        studentId,
        reservationDate: date,
        timeSlot,
        reviewStatus: status
      })
      alert('审核成功')
      fetchPendingReservations()
      fetchHistoryReservations(selectedDate)
    } catch (err) {
      alert('审核失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const formatDate = (dateStr) => {
    return dateStr ? dayjs(dateStr).format('YYYY-MM-DD') : ''
  }

  const handleDateChange = (e) => {
    const newDate = e.target.value
    setSelectedDate(newDate)
    fetchHistoryReservations(newDate)
  }

  return (
    <div className="study-room-reservation-wrapper">
      <div className="study-room-reservation-top-bar">
        <button className="study-room-reservation-back-button" onClick={() => navigate('/admin/admin-study-room')}>
          返回
        </button>
        <h2 className="study-room-reservation-title">审核预约记录</h2>
        <div className="study-room-reservation-switch-buttons">
          <button
            className={view === 'pending' ? 'active' : ''}
            onClick={() => setView('pending')}
          >
            待审核预约
          </button>
          <button
            className={view === 'history' ? 'active' : ''}
            onClick={() => setView('history')}
          >
            历史预约
          </button>
        </div>
      </div>

      <div className="study-room-reservation-content">
        {view === 'pending' ? (
          <div>
            {pendingReservations.length > 0 ? (
              <table className="reservation-table">
                <thead>
                  <tr>
                    <th>自习室</th>
                    <th>位置</th>
                    <th>容量</th>
                    <th>学员姓名</th>
                    <th>预约日期</th>
                    <th>时段</th>
                    <th>审核状态</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  {pendingReservations.map((r, index) => (
                    <tr key={index}>
                      <td>{r.roomName}</td>
                      <td>{r.location || '-'}</td>
                      <td>{r.capacity || '-'}</td>
                      <td>{r.studentName}</td>
                      <td>{formatDate(r.reservationDate)}</td>
                      <td>{r.timeSlot}</td>
                      <td>{r.reviewStatus}</td>
                      <td>
                        <button
                          className="review-button approve"
                          onClick={() => handleReview(r.roomId, r.studentId, r.reservationDate, r.timeSlot, '通过')}
                        >
                          通过
                        </button>
                        <button
                          className="review-button reject"
                          onClick={() => handleReview(r.roomId, r.studentId, r.reservationDate, r.timeSlot, '驳回')}
                        >
                          驳回
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>暂无待审核预约记录</p>
            )}
          </div>
        ) : (
          <div>
            <div className="date-picker-wrapper">
              <label>选择日期：</label>
              <input
                type="date"
                value={selectedDate}
                onChange={handleDateChange}
              />
            </div>

            {historyReservations.length > 0 ? (
              <table className="reservation-table">
                <thead>
                  <tr>
                    <th>自习室</th>
                    <th>位置</th>
                    <th>容量</th>
                    <th>学员姓名</th>
                    <th>预约日期</th>
                    <th>时段</th>
                    <th>审核状态</th>
                  </tr>
                </thead>
                <tbody>
                  {historyReservations.map((r, index) => (
                    <tr key={index}>
                      <td>{r.roomName}</td>
                      <td>{r.location || '-'}</td>
                      <td>{r.capacity || '-'}</td>
                      <td>{r.studentName}</td>
                      <td>{formatDate(r.reservationDate)}</td>
                      <td>{r.timeSlot}</td>
                      <td>{r.reviewStatus}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>该日期暂无历史通过记录</p>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminStudyRoomReservation