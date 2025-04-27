import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './admin_study_room_list.css'

const AdminStudyRoomList = () => {
  const navigate = useNavigate()
  const [studyRooms, setStudyRooms] = useState([])
  const [showModal, setShowModal] = useState(false)
  const [isEditing, setIsEditing] = useState(false)
  const [roomForm, setRoomForm] = useState({ roomId: null, roomName: '', capacity: '', location: '' })

  useEffect(() => {
    fetchStudyRooms()
  }, [])

  const fetchStudyRooms = async () => {
    try {
      const res = await axios.get('/api/study-rooms/all')
      setStudyRooms(res.data.data || [])
    } catch (err) {
      alert('获取自习室列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const openEditModal = (room) => {
    setIsEditing(true)
    setRoomForm({
      roomId: room.roomId,
      roomName: room.roomName,
      capacity: room.capacity,
      location: room.location
    })
    setShowModal(true)
  }

  const openCreateModal = () => {
    setIsEditing(false)
    setRoomForm({ roomId: null, roomName: '', capacity: '', location: '' })
    setShowModal(true)
  }

  const handleDelete = async (roomId) => {
    if (!window.confirm('确定要删除该自习室吗？')) return
    try {
      await axios.delete(`/api/study-rooms/delete/${roomId}`)
      alert('删除成功')
      fetchStudyRooms()
    } catch (err) {
      alert('删除失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleSubmit = async () => {
    if (!roomForm.roomName.trim() || !roomForm.capacity || !roomForm.location.trim()) {
      alert('请填写完整信息')
      return
    }

    try {
      if (isEditing) {
        await axios.put('/api/study-rooms/update', roomForm)
        alert('修改成功')
      } else {
        await axios.post('/api/study-rooms', roomForm)
        alert('录入成功')
      }
      setShowModal(false)
      fetchStudyRooms()
    } catch (err) {
      alert('提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="study-room-list-wrapper">
      <button className="back-button" onClick={() => navigate('/admin/admin-study-room')}>返回</button>
      <h2 className="title">自习室列表管理</h2>

      <div className="section-header">
        <button className="sr-small-button" onClick={openCreateModal}>录入新自习室</button>
      </div>

      <table className="study-room-table">
        <thead>
          <tr>
            <th>自习室ID</th>
            <th>名称</th>
            <th>容量</th>
            <th>位置</th>
            <th>当前状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {studyRooms.length > 0 ? (
            studyRooms.map(room => (
              <tr key={room.roomId}>
                <td>{room.roomId}</td>
                <td>{room.roomName}</td>
                <td>{room.capacity}</td>
                <td>{room.location}</td>
                <td>{room.currentStatus || '-'}</td>
                <td>
                  <div className="action-buttons">
                    <button className="small-button" onClick={() => openEditModal(room)}>修改</button>
                    <button className="small-button" onClick={() => handleDelete(room.roomId)}>删除</button>
                  </div>
                </td>
              </tr>
            ))
          ) : (
            <tr><td colSpan="6">暂无自习室</td></tr>
          )}
        </tbody>
      </table>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>{isEditing ? '修改自习室信息' : '录入新自习室'}</h3>

            <input
              type="text"
              placeholder="自习室名称"
              value={roomForm.roomName}
              onChange={(e) => setRoomForm({ ...roomForm, roomName: e.target.value })}
            />
            <input
              type="number"
              placeholder="容量"
              value={roomForm.capacity}
              onChange={(e) => setRoomForm({ ...roomForm, capacity: e.target.value })}
            />
            <input
              type="text"
              placeholder="位置"
              value={roomForm.location}
              onChange={(e) => setRoomForm({ ...roomForm, location: e.target.value })}
            />

            <div className="modal-buttons">
              <button onClick={handleSubmit}>{isEditing ? '提交修改' : '提交录入'}</button>
              <button onClick={() => setShowModal(false)}>取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default AdminStudyRoomList