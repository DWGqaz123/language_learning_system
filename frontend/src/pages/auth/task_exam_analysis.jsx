import React, { useEffect, useState } from 'react'
import './task_exam_analysis.css'

const TaskExamAnalysis = () => {
  const [taskStats, setTaskStats] = useState(null)
  const [examReport, setExamReport] = useState(null)
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchTaskStats()
    fetchExamReport()
  }, [])

  const fetchTaskStats = async () => {
    try {
      const res = await fetch(`/api/task-assignments/student/${userInfo.userId}/stats`)
      const data = await res.json()
      setTaskStats(data.data)
    } catch (error) {
      console.error('加载任务统计数据失败', error)
    }
  }

  const fetchExamReport = async () => {
    try {
      // 注意这里需要 examId 和 studentId
      const examId = 1 // TODO: 实际项目中需要动态获取最新的 examId
      const res = await fetch(`/api/mock-exams/exam-report?examId=${examId}&studentId=${userInfo.userId}`)
      const data = await res.json()
      setExamReport(data.data)
    } catch (error) {
      console.error('加载考试报告数据失败', error)
    }
  }

  return (
    <div className="tasks-exams-analysis">
      <div className="tasks-section">
        <h3>课后任务分析</h3>
        <div className="task-metrics">
          <div>
            <span className="metric-value">{taskStats ? `${taskStats.onTimeCompletionRate}%` : '...'}</span>
            <span className="metric-label">按时完成率</span>
          </div>
          <div>
            <span className="metric-value">{taskStats ? `${taskStats.averageScore}` : '...'}</span>
            <span className="metric-label">平均得分</span>
          </div>
          <div>
            <span className="metric-value">{taskStats ? `${taskStats.avgSubmitAdvanceDays}天` : '...'}</span>
            <span className="metric-label">平均提交提前时间</span>
          </div>
        </div>
        <div className="task-trend">
          {/* 可绘制任务得分趋势图 */}
          <p>（任务得分趋势图开发中）</p>
        </div>
      </div>

      <div className="exams-section">
        <h3>模拟考试分析</h3>
        <div className="exam-scores">
          {/* 可绘制各科目得分条形图 */}
          <p>{examReport ? `最近成绩: ${examReport.totalScore}` : '加载中...'}</p>
          <div className="exam-comments">
            <h4>教师反馈</h4>
            <p>{examReport ? (examReport.teacherComment || '暂无教师点评') : '加载中...'}</p>
            <h4>助教反馈</h4>
            <p>{examReport ? (examReport.assistantComment || '暂无助教点评') : '加载中...'}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default TaskExamAnalysis