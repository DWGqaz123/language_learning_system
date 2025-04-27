import React, { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import axios from 'axios'
import './teacher_resource.css'

const TeacherResourceManage = () => {
  const navigate = useNavigate()
  const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  const [resources, setResources] = useState([])
  const [categories, setCategories] = useState([])

  useEffect(() => {
    fetchResources()
    fetchCategories()
  }, [])

  const fetchResources = async () => {
    try {
      const res = await axios.get('/api/resource-categories/published')
      setResources(res.data.data || [])
    } catch (err) {
      alert('获取资源失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const fetchCategories = async () => {
    try {
      const res = await axios.get('/api/resource-categories')
      setCategories(res.data.data || [])
    } catch (err) {
      alert('获取资源类目失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const groupResourcesByCategory = () => {
    const grouped = {}
    resources.forEach(resource => {
      const category = resource.categoryName || '未分类'
      if (!grouped[category]) {
        grouped[category] = []
      }
      grouped[category].push(resource)
    })
    return grouped
  }

  const groupedResources = groupResourcesByCategory()

  return (
    <div className="teacher-layout">
      {/* 左侧导航栏 */}
      <aside className="sider">
        <div className="avatar">
          <div className="circle">教</div>
          <div className="label">教师: {storedUser.username || '...'}</div>
          <div className="label">用户ID: {storedUser.userId || '...'}</div>
          <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/teacher/teacher-user">个人中心</Link></li>
          <li><Link to="/teacher/teacher-course">课程管理</Link></li>
          <li><Link to="/teacher/teacher-task">课后管理</Link></li>
          <li><Link to="/teacher/teacher-exam">模拟考试管理</Link></li>
          <li><Link to="/teacher/teacher-study-room">自习室管理</Link></li>
          <li className="active"><Link to="/teacher/teacher-resource">资源管理</Link></li>
          <li><Link to="/teacher/teacher-analytics">学习分析</Link></li>
          <li><Link to="/teacher/teacher-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      {/* 主内容区 */}
      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，教师 {storedUser.username || '...'}！</h2>
        </header>
        <section className="content">

          {Object.keys(groupedResources).map(category => (
            <div key={category} className="teacher-resource-wrapper-category-block">
              <h3 className="teacher-resource-wrapper-category-title">{category}</h3>
              <div className="teacher-resource-wrapper-resource-grid">
                {groupedResources[category].map(resource => (
                  <div key={resource.resourceId} className="teacher-resource-wrapper-resource-card">
                    <h4>{resource.resourceName}</h4>
                    <p><strong>类型：</strong>{resource.resourceType || '-'}</p>
                    <p><strong>上传时间：</strong>{resource.uploadTime?.replace('T', ' ').split('.')[0] || '-'}</p>
                    <p><strong>下载次数：</strong>{resource.downloadCount ?? 0}</p>
                    <p><strong>上传人：</strong>{resource.uploaderName || '-'}</p>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </section>
      </main>
    </div>
  )
}

export default TeacherResourceManage