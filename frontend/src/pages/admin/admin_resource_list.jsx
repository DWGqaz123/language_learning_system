import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './admin_resource_list.css'
import dayjs from 'dayjs'

const AdminResourceList = () => {
  const navigate = useNavigate()
  const [resources, setResources] = useState([])

  useEffect(() => {
    fetchResources()
  }, [])

  const fetchResources = async () => {
    try {
      const res = await axios.get('/api/resource-categories/published') // 确认你的查询资源接口路径
      if (res.data.code === 200) {
        setResources(res.data.data || [])
      } else {
        alert('查询资源失败: ' + (res.data.message || '未知错误'))
      }
    } catch (err) {
      alert('查询资源失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleDelete = async (resourceId) => {
    if (!window.confirm('确定要删除该资源吗？')) return
    try {
      const res = await axios.delete(`/api/resources/${resourceId}`)
      if (res.data.code === 200) {
        alert('删除成功')
        fetchResources()
      } else {
        alert('删除失败: ' + (res.data.message || '未知错误'))
      }
    } catch (err) {
      alert('删除失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleEdit = (resource) => {
    const newName = prompt('请输入新的资源名称', resource.resourceName)
    if (!newName) return

    const newType = prompt('请输入新的资源类型（如：PDF、视频、音频）', resource.resourceType)
    if (!newType) return

    const newContent = prompt('请输入新的资源内容链接', resource.resourceContent || '')
    if (!newContent) return

    // 注意：这里只能修改基本信息，不能修改审核状态、上传人、上传时间
    const updateData = {
      resourceId: resource.resourceId,
      resourceName: newName,
      resourceType: newType,
      resourceContent: newContent,
      // 后端如果需要categoryId的话，需要处理一下。这里只修改名称等基本信息
    }

    axios.put('/api/resources', updateData)
      .then(res => {
        if (res.data.code === 200) {
          alert('修改成功')
          fetchResources()
        } else {
          alert('修改失败: ' + (res.data.message || '未知错误'))
        }
      })
      .catch(err => {
        alert('修改失败: ' + (err.response?.data?.message || '未知错误'))
      })
  }

  const formatTime = (timeStr) => {
    if (!timeStr) return ''
    return dayjs(timeStr).format('YYYY-MM-DD HH:mm')
  }

  return (
    <div className="resource-list-wrapper">
      <button className="back-button" onClick={() => navigate('/admin/admin-resource')}>返回</button>
      <h2 className="title">资源列表管理</h2>

      {resources.length > 0 ? (
        <table className="resource-table">
          <thead>
            <tr>
              <th>资源ID</th>
              <th>资源名称</th>
              <th>类型</th>
              <th>分类名称</th>
              <th>上传人</th>
              <th>上传时间</th>
              <th>审核状态</th>
              <th>下载次数</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {resources.map((r, index) => (
              <tr key={index}>
                <td>{r.resourceId}</td>
                <td>{r.resourceName}</td>
                <td>{r.resourceType}</td>
                <td>{r.categoryName}</td>
                <td>{r.uploaderName}</td>
                <td>{formatTime(r.uploadTime)}</td>
                <td>{r.auditStatus}</td>
                <td>{r.downloadCount ?? 0}</td>
                <td>
                  <button className="action-button edit" onClick={() => handleEdit(r)}>编辑</button>
                  <button className="action-button delete" onClick={() => handleDelete(r.resourceId)}>删除</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p className="no-data-text">暂无资源数据</p>
      )}
    </div>
  )
}

export default AdminResourceList