import React, { useEffect, useState } from 'react';
import './assistant_analytics.css';
import { Link } from 'react-router-dom';
import axios from 'axios';

const AssistantAnalytics = () => {
  const phone = sessionStorage.getItem('phone') || '';
  const [userInfo, setUserInfo] = useState(null);
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState(null);
  const [reports, setReports] = useState([]);
  const [studentInfo, setStudentInfo] = useState(null);
  const [studyRoomStats, setStudyRoomStats] = useState(null);
  const [examTrend, setExamTrend] = useState([]);
  const [commentInputs, setCommentInputs] = useState({
    assistantComment: '',
    overallScore: ''
  });

  const [courseProgress, setCourseProgress] = useState(null);
  const [attendanceStats, setAttendanceStats] = useState(null);
  const [taskStats, setTaskStats] = useState(null);
  const [examStats, setExamStats] = useState(null);

  useEffect(() => {
    if (phone) {
      axios.get(`/api/users/phone`, { params: { phoneNumber: phone } })
        .then(res => {
          if (res.data.code === 200) {
            setUserInfo(res.data.data);
          }
        })
        .catch(err => {
          console.error('获取助教信息失败:', err);
        });
    }
    fetchStudents();
  }, [phone]);

  const fetchStudents = async () => {
    try {
      const res = await axios.get('/api/users/all-students');
      setStudents(res.data.data || []);
    } catch (err) {
      console.error('获取学员列表失败:', err);
    }
  };

  const handleSelectStudent = async (studentId) => {
    setSelectedStudentId(studentId);
    try {
      const userRes = await axios.get(`/api/users/get-by-id?userId=${studentId}`);
      setStudentInfo(userRes.data.data || {});
      setCommentInputs({ assistantComment: '', overallScore: '' });
      fetchStudentReports(studentId);

      // 并发调用所有学习数据接口
      const [cpRes, attRes, taskRes, examRes] = await Promise.all([
        axios.get(`/api/analysis/course-progress?studentId=${studentId}`),
        axios.get(`/api/analysis/attendance-stats?studentId=${studentId}`),
        axios.get(`/api/analysis/task-statistics/${studentId}`),
        axios.get(`/api/analysis/mock-exams/statistics/${studentId}`)
      ]);
      const [studyRoomRes, trendRes] = await Promise.all([
        axios.get(`/api/study-rooms/usage-statistics/${studentId}`),
        axios.get(`/api/analysis/mock-exams/score-trend/${studentId}`)
      ]);

      setStudyRoomStats(studyRoomRes.data.data || null);
      setExamTrend(trendRes.data.data || []);

      setCourseProgress(cpRes.data.data || null);
      setAttendanceStats(attRes.data.data || null);
      setTaskStats(taskRes.data.data || null);
      setExamStats(examRes.data.data || null);
    } catch (err) {
      alert('获取学员信息或学习数据失败: ' + (err.response?.data?.message || '未知错误'));
    }
  };

  const fetchStudentReports = async (studentId) => {
    try {
      const res = await axios.get(`/api/analysis/student-reports?studentId=${studentId}`);
      const sortedReports = (res.data.data || []).sort((a, b) => new Date(b.generatedTime) - new Date(a.generatedTime));
      setReports(sortedReports);
    } catch (err) {
      console.error('获取学员报告失败:', err);
    }
  };

  const calculateRegisterDays = (createdAt) => {
    if (!createdAt) return '-';
    const created = new Date(createdAt);
    const now = new Date();
    const diffTime = now - created;
    return Math.floor(diffTime / (1000 * 60 * 60 * 24)) + ' 天';
  };

  const handleSubmitReport = async () => {
    if (!selectedStudentId) {
      alert('请先选择学员');
      return;
    }
    if (!commentInputs.assistantComment || !commentInputs.overallScore) {
      alert('请填写完整的助教点评和综合评分');
      return;
    }

    try {
      const payload = {
        studentId: selectedStudentId,
        overallScore: parseFloat(commentInputs.overallScore),
        assistantComment: commentInputs.assistantComment
      };

      await axios.post('/api/analysis/generate-report', payload);
      alert('学习表现报告生成成功');
      fetchStudentReports(selectedStudentId);
      setCommentInputs({ assistantComment: '', overallScore: '' });
    } catch (err) {
      alert('生成报告失败: ' + (err.response?.data?.message || '未知错误'));
    }
  };

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
                  <p><strong>课程进度：</strong>
                    {courseProgress
                      ? `${courseProgress.completedHours}/${courseProgress.totalHours} 已完成（${courseProgress.completedPercentage}%）`
                      : '加载中...'}</p>
                  <p><strong>考勤统计：</strong>
                    {attendanceStats
                      ? `出勤 ${attendanceStats.attendCount} 次，缺勤 ${attendanceStats.absentCount} 次，请假 ${attendanceStats.leaveCount} 次`
                      : '加载中...'}</p>
                  <p><strong>任务表现：</strong>
                    {taskStats
                      ? `完成率 ${taskStats.completionRate?.toFixed(1)}%，平均分 ${taskStats.averageScore?.toFixed(1) ?? '暂无'}`
                      : '加载中...'}</p>
                  <p><strong>模拟考试：</strong>
                    {examStats
                      ? `考试 ${examStats.examCount} 次，平均分 ${examStats.averageScore?.toFixed(1)}`
                      : '加载中...'}</p>
                  <p><strong>考试成绩趋势：</strong></p>
                  <ul>
                    {examTrend.length > 0
                      ? examTrend.map((item, idx) => (
                        <li key={idx}>{item.examName}：{item.score} 分</li>
                      ))
                      : <li>暂无成绩记录</li>}
                  </ul>
                  <p><strong>自习室统计：</strong>
                    {studyRoomStats
                      ? `总计使用 ${studyRoomStats.totalUsageCount} 次，其中 上午 ${studyRoomStats.morningCount} 次，下午 ${studyRoomStats.afternoonCount} 次，晚上 ${studyRoomStats.eveningCount} 次`
                      : '加载中...'}
                  </p>


                </div>
              )}

              {selectedStudentId && (
                <div className="comment-section">
                  <textarea
                    className="comment-input"
                    placeholder="请输入助教点评..."
                    value={commentInputs.assistantComment}
                    onChange={e => setCommentInputs(prev => ({ ...prev, assistantComment: e.target.value }))}
                  />
                  <input
                    className="score-input"
                    type="number"
                    placeholder="请输入综合评分（0-100）"
                    min="0"
                    max="100"
                    step="0.1"
                    value={commentInputs.overallScore}
                    onChange={e => setCommentInputs(prev => ({ ...prev, overallScore: e.target.value }))}
                  />
                  <button onClick={handleSubmitReport}>生成学习表现报告</button>
                </div>
              )}

              {reports.length > 0 && (
                <div className="history-report-section">
                  <h3>历史学习报告</h3>
                  {reports.map(report => (
                    <div key={report.reportId} className="report-box">
                      <p><strong>生成时间：</strong>{report.generatedTime?.slice(0, 10) || '-'}</p>
                      <p><strong>综合评分：</strong>{report.overallScore ?? '-'} 分</p>
                      <p><strong>助教点评：</strong>{report.assistantComment || '-'}</p>
                      <p><strong>出勤总结：</strong>{report.attendanceSummary || '-'}</p>
                      <p><strong>任务总结：</strong>{report.taskSummary || '-'}</p>
                      <p><strong>考试总结：</strong>{report.examSummary || '-'}</p>
                      <p><strong>自习室总结：</strong>{report.studyRoomSummary || '-'}</p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

export default AssistantAnalytics;