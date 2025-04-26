import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './assistant.css'
import './assistant_resource.css'
import { Link } from 'react-router-dom'

const AssistantResource = () => {
  const [userInfo, setUserInfo] = useState(null)
  const [categories, setCategories] = useState([])
  const [selectedCategoryId, setSelectedCategoryId] = useState('')
  const [uploadForm, setUploadForm] = useState({
    resourceName: '',
    resourceType: '',
    resourceContent: '',
    categoryId: ''
  })
  const [resourceList, setResourceList] = useState([])

  const phone = sessionStorage.getItem('phone') || ''

  useEffect(() => {
    if (phone) {
      axios.get(`/api/users/phone`, { params: { phoneNumber: phone } })
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data)
          }
        })
    }
    fetchCategories()
  }, [phone])

  useEffect(() => {
    fetchResources()
  }, [selectedCategoryId])

  const fetchCategories = async () => {
    const res = await axios.get('/api/resource-categories')
    setCategories(res.data.data || [])
  }

  const fetchResources = async () => {
    const res = await axios.get('/api/resource-categories/published', {
      params: selectedCategoryId ? { categoryId: selectedCategoryId } : {}
    })
    setResourceList(res.data.data || [])
  }

  const handleUpload = async () => {
    if (!uploadForm.resourceName || !uploadForm.resourceType || !uploadForm.resourceContent || !uploadForm.categoryId) {
      alert('请完整填写所有字段')
      return
    }

    try {
      await axios.post('/api/resource-categories/upload', {
        ...uploadForm,
        uploaderId: userInfo.userId
      })
      alert('资源上传成功，等待审核')
      setUploadForm({ resourceName: '', resourceType: '', resourceContent: '', categoryId: '' })
      fetchResources()
    } catch (err) {
      alert('上传失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="layout">
      <aside className="sider">
        <div className="avatar">
          <div className="circle">助</div>
          <div className="label">助教: {userInfo ? userInfo.username : '...'}</div>
          <div className="label">用户Id: {userInfo ? userInfo.userId : '...'}</div>
          <div className="label">账号: {userInfo ? userInfo.phoneNumber : '...'}</div>
        </div>
        <ul className="menu">
          <li><Link to="/assistant/assistant-user">用户管理</Link></li>
          <li><Link to="/assistant/assistant-course">课程管理</Link></li>
          <li><Link to="/assistant/assistant-task">课后任务管理</Link></li>
          <li><Link to="/assistant/assistant-exam">模拟考试管理</Link></li>
          <li><Link to="/assistant/assistant-study-room">自习室管理</Link></li>
          <li className="active"><Link to="/assistant/assistant-resource">资源管理</Link></li>
          <li><Link to="/assistant/assistant-analytics">学习分析</Link></li>
          <li><Link to="/assistant/assistant-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      <main className="main">
        <header className="header">
          <h2>语言学习机构后台管理系统</h2>
          <h2>欢迎您，助教 {userInfo ? userInfo.username : '...'}！请上传教学资源或查看资源列表。</h2>
        </header>

        <section className="content">
          <div className="card-grid two-cols">
            <div className="card upload-section">
              <h3>上传教学资源</h3>
              <label>资源名称：</label>
              <input
                value={uploadForm.resourceName}
                onChange={e => setUploadForm(prev => ({ ...prev, resourceName: e.target.value }))}
              />
              <label>资源名称：</label>
              <input
                value={uploadForm.resourceType}
                onChange={e => setUploadForm(prev => ({ ...prev, resourceType: e.target.value }))}
              />
              <label>资源类目：</label>
              <select
                value={uploadForm.categoryId}
                onChange={e => setUploadForm(prev => ({ ...prev, categoryId: e.target.value }))}
              >
                <option value="">请选择类目</option>
                {categories.map(cat => (
                  <option key={cat.categoryId} value={cat.categoryId}>{cat.categoryName}</option>
                ))}
              </select>
              <label>资源内容：</label>
              <textarea
                rows={5}
                value={uploadForm.resourceContent}
                onChange={e => setUploadForm(prev => ({ ...prev, resourceContent: e.target.value }))}
              />
              <button onClick={handleUpload}>上传资源</button>
            </div>

            <div className="card resource-list-section">
              <h3>已发布资源</h3>
              <label>筛选类目：</label>
              <select
                value={selectedCategoryId}
                onChange={e => setSelectedCategoryId(e.target.value)}
              >
                <option value="">全部</option>
                {categories.map(cat => (
                  <option key={cat.categoryId} value={cat.categoryId}>{cat.categoryName}</option>
                ))}
              </select>
              <table className="resource-table">
                <thead>
                  <tr>
                    <th>名称</th>
                    <th>类型</th>
                    <th>类目</th>
                    <th>上传者</th>
                    <th>上传时间</th>
                    <th>下载次数</th>
                  </tr>
                </thead>
                <tbody>
                  {resourceList.map(resource => (
                    <tr key={resource.resourceId}>
                      <td>{resource.resourceName}</td>
                      <td>{resource.resourceType}</td>
                      <td>{resource.categoryName}</td>
                      <td>{resource.uploaderName}</td>
                      <td>{resource.uploadTime}</td>
                      <td>{resource.downloadCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AssistantResource