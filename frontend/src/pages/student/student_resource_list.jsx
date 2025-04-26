import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './student_resource_list.css'

const StudentResourceList = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')
  const [resources, setResources] = useState([])

  useEffect(() => {
    fetchResources()
  }, [])

  const fetchResources = async () => {
    try {
      const res = await axios.get('/api/resource-categories/published')
      setResources(res.data.data || [])
    } catch (err) {
      alert('获取资源列表失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleDownload = async (resourceId, resourceName) => {
    try {
      const res = await axios.get(`/api/resource-categories/download/${resourceId}`)
      const fileUrl = res.data.data

      const link = document.createElement('a')
      link.href = fileUrl
      link.download = resourceName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (err) {
      alert('下载失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const groupedResources = {}
  resources.forEach(r => {
    const category = r.categoryName || '未分类'
    if (!groupedResources[category]) {
      groupedResources[category] = []
    }
    groupedResources[category].push(r)
  })

  return (
    <div className="resource-wrapper">
        <button className="back-button" onClick={() => navigate('/student/student-resource')}>返回</button>
        <h2 className='title'>教学资源列表</h2>

      {Object.keys(groupedResources).map(category => (
        <div key={category} className="category-section">
          <h3>{category}</h3>
          <div className="resource-grid">
            {groupedResources[category].map(resource => (
              <div key={resource.resourceId} className="resource-card">
                <h4>{resource.resourceName}</h4>
                <p><strong>上传者：</strong>{resource.uploaderName || '-'}</p>
                <p><strong>上传时间：</strong>{resource.uploadTime?.replace('T', ' ') || '-'}</p>
                <p><strong>下载次数：</strong>{resource.downloadCount || 0}</p>
                <button className="download-btn" onClick={() => handleDownload(resource.resourceId, resource.resourceName)}>下载</button>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}

export default StudentResourceList