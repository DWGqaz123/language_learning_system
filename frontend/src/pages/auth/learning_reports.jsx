import React, { useEffect, useState } from 'react'
import './learning_reports.css'

const LearningReports = () => {
  const [currentReport, setCurrentReport] = useState(null)
  const [reportHistory, setReportHistory] = useState([])
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchCurrentReport()
    // 如果后端有 student-reports 接口，再启用下面这行
    // fetchReportHistory()
  }, [])

  const fetchCurrentReport = async () => {
    try {
      // 注意这里是 reportId，不是 studentId
      const reportId = 1  // TODO: 这里需要动态获取对应 reportId，目前先写死测试
      const res = await fetch(`/api/analysis/report?reportId=${reportId}`)
      const data = await res.json()
      setCurrentReport(data.data)
    } catch (error) {
      console.error('加载当前学习报告失败', error)
    }
  }

  const fetchReportHistory = async () => {
    try {
      const res = await fetch(`/api/analysis/student-reports?studentId=${userInfo.userId}`)
      const data = await res.json()
      setReportHistory(data.data || [])
    } catch (error) {
      console.error('加载历史学习报告失败', error)
    }
  }

  return (
    <div className="reports-section">
      <h2>学习报告与建议</h2>

      <div className="current-report">
        <h3>最新学习报告</h3>
        {currentReport ? (
          <div className="report-card">
            <div className="report-summary">
              <p><strong>总体评分:</strong> {currentReport.overallScore}</p>
              <p><strong>出勤总结:</strong> {currentReport.attendanceSummary}</p>
              <p><strong>任务总结:</strong> {currentReport.taskSummary}</p>
              <p><strong>考试总结:</strong> {currentReport.examSummary}</p>
              <p><strong>自习总结:</strong> {currentReport.studyRoomSummary}</p>
            </div>
            <div className="report-details">
              <h4>助教点评</h4>
              <p>{currentReport.assistantComment}</p>
            </div>
          </div>
        ) : (
          <p>加载中...</p>
        )}
      </div>

      {/* 历史报告部分暂时保留，待确认接口后启用 */}
      {/* <div className="report-history">
        <h3>历史报告</h3>
        <table>
          <thead>
            <tr>
              <th>日期</th>
              <th>评分</th>
              <th>主要建议</th>
            </tr>
          </thead>
          <tbody>
            {reportHistory.length > 0 ? (
              reportHistory.map((report, index) => (
                <tr key={index}>
                  <td>{report.date}</td>
                  <td>{report.grade}</td>
                  <td>{report.mainSuggestion}</td>
                </tr>
              ))
            ) : (
              <tr><td colSpan="3">暂无历史记录</td></tr>
            )}
          </tbody>
        </table>
      </div> */}
    </div>
  )
}

export default LearningReports