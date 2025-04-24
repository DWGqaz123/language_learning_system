import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'
import './role_management.css'

const RoleManagement = () => {
  const [users, setUsers] = useState([])
  const [phoneQuery, setPhoneQuery] = useState('')
  const [modalMessage, setModalMessage] = useState('')
  const [showModal, setShowModal] = useState(false)
  const navigate = useNavigate()

  const fetchAllUsers = () => {
    axios.get('/api/users')
      .then(res => {
        if (res.data.code === 200) setUsers(res.data.data)
      })
  }

  useEffect(() => {
    fetchAllUsers()
  }, [])

  const handleSearch = () => {
    if (!phoneQuery) {
      fetchAllUsers()
      return
    }

    axios.get('/api/users/phone', {
      params: { phoneNumber: phoneQuery }
    }).then(res => {
      if (res.data.code === 200 && res.data.data) {
        setUsers([res.data.data])
      } else {
        setUsers([])
      }
    })
  }

  const handleRoleChange = (userId, roleId) => {
    const operatorUserId = localStorage.getItem('userId')
    axios.put('/api/users/update-role', {
      userId,
      roleId,
      operatorUserId: Number(operatorUserId)
    }).then(res => {
      if (res.data.code === 200) {
        setModalMessage('角色修改成功')
        setShowModal(true)
        fetchAllUsers()
      }
    }).catch(err => {
      setModalMessage('角色修改失败')
      setShowModal(true)
    })
  }

  return (
    <div className="role-wrapper">
      <button className="back-button" onClick={() => navigate('/admin/admin-user')}>← 返回</button>
      <h2 className='title'>用户列表查看</h2>

      <div className="search-section">
        <input
          type="text"
          placeholder="输入手机号搜索用户"
          value={phoneQuery}
          onChange={(e) => setPhoneQuery(e.target.value)}
        />
        <button onClick={handleSearch}>搜索</button>
      </div>

      <table className="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>手机号</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {users.length > 0 ? users.map(user => (
            <tr key={user.userId}>
              <td>{user.userId}</td>
              <td>{user.username}</td>
              <td>{user.phoneNumber}</td>
              <td>{user.roleName}</td>
              <td>
                <select
                  defaultValue={user.roleId}
                  onChange={(e) => handleRoleChange(user.userId, Number(e.target.value))}
                >
                  <option value={1}>学员</option>
                  <option value={2}>教师</option>
                  <option value={3}>助教</option>
                  <option value={4}>管理员</option>
                </select>
              </td>
            </tr>
          )) : <tr><td colSpan="5">未找到用户</td></tr>}
        </tbody>
      </table>

      {/* Modal 弹窗 */}
      {showModal && (
        <div className="modal">
          <div className="modal-content">
            <p>{modalMessage}</p>
            <button onClick={() => setShowModal(false)}>关闭</button>
          </div>
        </div>
      )}
    </div>
  )
}

export default RoleManagement