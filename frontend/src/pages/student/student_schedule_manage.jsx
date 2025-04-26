import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_schedule_manage.css'

const StudentScheduleManage = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const userId = storedUser.userId

  const [scheduleRecords, setScheduleRecords] = useState([])
  const [showLeaveModal, setShowLeaveModal] = useState(false)
  const [selectedRecord, setSelectedRecord] = useState(null)
  const [leaveReason, setLeaveReason] = useState('')

  useEffect(() => {
    fetchScheduleRecords()
  }, [])

  const fetchScheduleRecords = async () => {
    try {
      const res = await axios.get(`/api/courses/schedule-records/${userId}`)
      setScheduleRecords(res.data.data || [])
    } catch (err) {
      alert('获取课表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openLeaveModal = (record) => {
    setSelectedRecord(record)
    setLeaveReason('')
    setShowLeaveModal(true)
  }

  const submitLeaveRequest = async () => {
    if (!leaveReason.trim()) {
      alert('请填写请假理由')
      return
    }
    try {
      await axios.post('/api/courses/student/leave-request', {
        ssrId: selectedRecord.ssrId,
        leaveReason: leaveReason
      })
      alert('请假申请已提交')
      setShowLeaveModal(false)
      fetchScheduleRecords()
    } catch (err) {
      alert('请假失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="schedule-wrapper">
        <button className="back-button" onClick={() => navigate('/student/student-course')}>返回</button>
        <h2 className='title' >查看个人课表</h2>

      <div className="schedule-table-wrapper">
        <table className="schedule-table">
          <thead>
            <tr>
              <th>上课时间</th>
              <th>课程名称</th>
              <th>教室</th>
              <th>教师</th>
              <th>助教</th>
              <th>请假状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {scheduleRecords.length > 0 ? (
              scheduleRecords.map(record => (
                <tr key={record.ssrId}>
                  <td>{record.classTime?.replace('T', ' ') || '-'}</td>
                  <td>{record.courseName || '-'}</td>
                  <td>{record.roomName || '-'}</td>
                  <td>{record.teacherName || '-'}</td>
                  <td>{record.assistantName || '-'}</td>
                  <td>{record.attendStatus === '请假待批' || record.attendStatus === '请假' ? record.attendStatus : '-'}</td>
                  <td>
                    {record.attendStatus === '未开始' && (
                      <button className="action-button" onClick={() => openLeaveModal(record)}>申请请假</button>
                    )}
                    {record.attendStatus !== '未开始' && <span className="disabled-text">不可请假</span>}
                  </td>
                </tr>
              ))
            ) : (
              <tr><td colSpan="7" className="no-data">暂无排课安排</td></tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 请假申请弹窗 */}
      {showLeaveModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>申请请假</h3>
            <textarea
              value={leaveReason}
              onChange={(e) => setLeaveReason(e.target.value)}
              placeholder="请填写请假理由"
            />
            <div className="modal-buttons">
              <button onClick={submitLeaveRequest}>提交申请</button>
              <button onClick={() => setShowLeaveModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default StudentScheduleManage