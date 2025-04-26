import React, { useEffect, useState } from 'react'
import './assistant_analytics.css'
import { Link } from 'react-router-dom'
import axios from 'axios'

const AssistantAnalytics = () => {
  const phone = sessionStorage.getItem('phone') || ''
  const [userInfo, setUserInfo] = useState(null)
  const [students, setStudents] = useState([])
  const [selectedStudentId, setSelectedStudentId] = useState(null)
  const [reports, setReports] = useState([])
  const [studentInfo, setStudentInfo] = useState(null)
  const [commentInputs, setCommentInputs] = useState({}) // <-- 改成对象，按reportId管理每个输入框内容

  useEffect(() => {
    if (phone) {
      axios.get(`/api/users/phone`, { params: { phoneNumber: phone } })
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data)
          }
        })
        .catch(err => {
          console.error('获取助教信息失败:', err)
        })
    }
    fetchStudents()
  }, [phone])

  const fetchStudents = async () => {
    const res = await axios.get('/api/users/all-students')
    setStudents(res.data.data || [])
  }

  const handleSelectStudent = async (studentId) => {
    setSelectedStudentId(studentId)
    try {
      await axios.post('/api/analysis/generate-report', { studentId })

      const res = await axios.get(`/api/analysis/student-reports?studentId=${studentId}`)
      const reportList = res.data.data || []
      setReports(reportList)

      const userRes = await axios.get(`/api/users/get-by-id?userId=${studentId}`)
      setStudentInfo(userRes.data.data || {})

      // 初始化每份报告的点评输入框
      const initialComments = {}
      reportList.forEach(r => {
        initialComments[r.reportId] = r.assistantComment || ''
      })
      setCommentInputs(initialComments)
    } catch (err) {
      alert('获取数据失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const calculateRegisterDays = (createdAt) => {
    if (!createdAt) return '-'
    const created = new Date(createdAt)
    const now = new Date()
    const diffTime = now - created
    return Math.floor(diffTime / (1000 * 60 * 60 * 24)) + ' 天'
  }

  const handleSaveComment = async (reportId) => {
    if (!commentInputs[reportId]?.trim()) {
      alert('请输入点评内容')
      return
    }
    try {
      await axios.put('/api/analysis/update-comment', {
        reportId,
        assistantComment: commentInputs[reportId]
      })
      alert('点评更新成功')
    } catch (err) {
      alert('点评提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  const handleCommentChange = (reportId, value) => {
    setCommentInputs(prev => ({
      ...prev,
      [reportId]: value
    }))
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
          <li><Link to="/assistant/assistant-resource">资源管理</Link></li>
          <li className="active"><Link to="/assistant/assistant-analytics">学习分析</Link></li>
          <li><Link to="/assistant/assistant-notification">通知中心</Link></li>
          <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
        </ul>
      </aside>

      <main className="main">
        <header className="header">
          <h2>学习分析模块</h2>
          <h2>欢迎您，助教 {userInfo ? userInfo.username : '...'}！请选择左侧学员查看学习报告。</h2>
        </header>

        <section className="content">
          <div className="analytics-layout">
            <div className="a-student-list">
              {students.map(s => (
                <div
                  key={s.userId}
                  className={`student-card ${s.userId === selectedStudentId ? 'selected' : ''}`}
                  onClick={() => handleSelectStudent(s.userId)}
                >
                  <div className="avatar-circle">{s.username?.charAt(0)}</div>
                  <div className="info">
                    <div className="name">{s.username}</div>
                    <div className="id">ID: {s.userId}</div>
                  </div>
                </div>
              ))}
            </div>

            <div className="report-section">
              {studentInfo && (
                <div className="student-summary">
                  <p><strong>姓名：</strong>{studentInfo.username || '-'}</p>
                  <p><strong>注册天数：</strong>{calculateRegisterDays(studentInfo.createdAt)}</p>
                </div>
              )}
              {reports.length > 0 ? (
                reports.map(report => (
                  <div key={report.reportId} className="report-box">
                    <p><strong>课程进度：</strong>{report.course?.completedPercentage ?? 0}%</p>
                    <p><strong>出勤记录：</strong>{report.attendance?.attendCount ?? 0} 出勤 / {report.attendance?.totalRecords ?? 0} 总课</p>
                    <p><strong>请假：</strong>{report.attendance?.leaveCount ?? 0} 次</p>
                    <p><strong>任务完成率：</strong>{report.task?.completionRate?.toFixed(1) ?? 0}%</p>
                    <p><strong>任务平均分：</strong>{report.task?.averageScore?.toFixed(1) ?? 0}</p>
                    <p><strong>考试次数：</strong>{report.mockExam?.examCount ?? 0}</p>
                    <p><strong>考试平均分：</strong>{report.mockExam?.averageScore?.toFixed(1) ?? 0}</p>
                    <p><strong>自习室使用：</strong>{report.studyRoom?.totalUsageCount ?? 0} 次</p>

                    <div className="comment-section">
                      <textarea
                        placeholder="输入助教点评..."
                        value={commentInputs[report.reportId] || ''}
                        onChange={e => handleCommentChange(report.reportId, e.target.value)}
                      />
                      <button onClick={() => handleSaveComment(report.reportId)}>提交点评</button>
                    </div>
                  </div>
                ))
              ) : (
                <p>请选择左侧学员以查看学习报告。</p>
              )}
            </div>
          </div>
        </section>
      </main>
    </div>
  )
}

export default AssistantAnalytics