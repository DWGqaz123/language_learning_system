import React, { useEffect, useState } from 'react'
import './study_plan.css'

const StudyPlan = () => {
  const [upcomingClasses, setUpcomingClasses] = useState([])
  const [pendingTasks, setPendingTasks] = useState([])
  const [goal, setGoal] = useState('12月前达到B2水平')
  const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

  useEffect(() => {
    fetchUpcomingClasses()
    fetchPendingTasks()
  }, [])

  const fetchUpcomingClasses = async () => {
    try {
      const res = await fetch(`/api/courses/my-schedules?userId=${userInfo.userId}`)
      const data = await res.json()
      setUpcomingClasses(data.data || [])
    } catch (error) {
      console.error('加载近期课程失败', error)
    }
  }

  const fetchPendingTasks = async () => {
    try {
      const res = await fetch(`/api/tasks/student/${userInfo.userId}/list`)
      const data = await res.json()
      setPendingTasks(data.data || [])
    } catch (error) {
      console.error('加载待完成任务失败', error)
    }
  }

  return (
    <div className="plan-section">
      <h2>我的学习计划</h2>

      <div className="upcoming-classes">
        <h3>近期课程</h3>
        <ul className="class-list">
          {upcomingClasses.length > 0 ? (
            upcomingClasses.map((course, index) => (
              <li key={index}>
                <span className="class-time">{course.classTime || '时间未定'}</span>
                <span className="class-name">{course.courseName || '课程名称未定'}</span>
              </li>
            ))
          ) : (
            <li>暂无近期课程</li>
          )}
        </ul>
      </div>

      <div className="pending-tasks">
        <h3>待完成任务</h3>
        <ul className="task-list">
          {pendingTasks.length > 0 ? (
            pendingTasks.map((task, index) => (
              <li key={index}>
                <input type="checkbox" id={`task-${index}`} />
                <label htmlFor={`task-${index}`}>{task.title || '未命名任务'}</label>
                <span className="due-date">截止: {task.deadline || '未设置'}</span>
              </li>
            ))
          ) : (
            <li>暂无待完成任务</li>
          )}
        </ul>
      </div>

      <div className="goal-setting">
        <h3>学习目标</h3>
        <div className="goal-progress">
          <p><strong>当前目标:</strong> {goal}</p>
          <div className="progress-bar">
            <div className="progress" style={{ width: '65%' }}></div>
          </div>
          <button className="edit-goal">编辑目标</button>
        </div>
      </div>
    </div>
  )
}

export default StudyPlan