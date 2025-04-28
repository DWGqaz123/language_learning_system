import React, { useEffect, useState } from 'react'
import './learning_reports.css'

const LearningReports = () => {
  const [currentReport, setCurrentReport] = useState(null)
  const [historyReports, setHistoryReports] = useState([])
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchCurrentReport()
    fetchHistoryReports()
  }, [])

  const fetchCurrentReport = async () => {
    try {
      const res = await fetch(`/api/analysis/latest-report?studentId=${userInfo.userId}`)
      const data = await res.json()
      setCurrentReport(data.data)
    } catch (error) {
      console.error('加载当前学习报告失败', error)
    }
  }

  const fetchHistoryReports = async () => {
    try {
      const res = await fetch(`/api/analysis/student-reports?studentId=${userInfo.userId}`)
      const data = await res.json()
      setHistoryReports(data.data || [])
    } catch (error) {
      console.error('加载历史学习报告失败', error)
    }
  }

  return (
    <div className="learning-reports">
      <h2>学习报告与建议</h2>

      <div className="report-section">
        <div className="current-report">
          <h3>最新学习报告</h3>
          {currentReport ? (
            <div className="report-card">
              <div className="report-summary">
                <p><strong>综合评分：</strong>{currentReport.overallScore ?? '-'}</p>
                <p><strong>助教点评：</strong>{currentReport.assistantComment || '暂无点评'}</p>
                <p><strong>生成时间：</strong>{currentReport.generatedTime?.slice(0, 10)}</p>
              </div>
              <div className="report-details">
                <h4>各项表现概览</h4>
                <ul>
                  <li><strong>出勤总结：</strong>{currentReport.attendanceSummary || '无'}</li>
                  <li><strong>任务总结：</strong>{currentReport.taskSummary || '无'}</li>
                  <li><strong>考试总结：</strong>{currentReport.examSummary || '无'}</li>
                  <li><strong>自习室总结：</strong>{currentReport.studyRoomSummary || '无'}</li>
                </ul>
              </div>
            </div>
          ) : (
            <p>加载最新学习报告中...</p>
          )}
        </div>

        <div className="history-reports">
          <h3>历史学习报告</h3>
          <table className="history-table">
            <thead>
              <tr>
                <th>生成时间</th>
                <th>综合评分</th>
                <th>助教点评</th>
              </tr>
            </thead>
            <tbody>
              {historyReports.length > 0 ? (
                historyReports.map((report, index) => (
                  <tr key={index}>
                    <td>{report.generatedTime?.slice(0, 10)}</td>
                    <td>{report.overallScore ?? '-'}</td>
                    <td>{report.assistantComment || '暂无点评'}</td>
                  </tr>
                ))
              ) : (
                <tr><td colSpan="3">暂无历史记录</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default LearningReports