import React, { useEffect, useState } from 'react'
import './exam_create.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const ExamCreate = () => {
    const navigate = useNavigate()
    const [examName, setExamName] = useState('')
    const [examTime, setExamTime] = useState('')
    const [selectedPaperId, setSelectedPaperId] = useState('')
    const [selectedRoomId, setSelectedRoomId] = useState('')
    const [students, setStudents] = useState([])
    const [selectedStudentIds, setSelectedStudentIds] = useState([])

    const [papers, setPapers] = useState([])
    const [rooms, setRooms] = useState([])

    useEffect(() => {
        fetchPapers()
        fetchRooms()
        fetchStudents()
    }, [])

    const fetchPapers = async () => {
        const res = await axios.get('/api/exams/papers/standard-papers')
        setPapers(res.data.data || [])
    }

    const fetchRooms = async () => {
        const res = await axios.get('/api/study-rooms/all')
        setRooms(res.data.data || [])
    }

    const fetchStudents = async () => {
        const res = await axios.get('/api/users/all-students')
        setStudents(res.data.data || [])
    }

    const toggleStudent = (id) => {
        if (selectedStudentIds.includes(id)) {
            setSelectedStudentIds(selectedStudentIds.filter(sid => sid !== id))
        } else {
            setSelectedStudentIds([...selectedStudentIds, id])
        }
    }

    const handleCreateExam = async () => {
        if (!examName || !examTime || !selectedPaperId || !selectedRoomId || selectedStudentIds.length === 0) {
            alert('请填写完整信息并选择至少一名学员')
            return
        }

        try {
            // 第一步：创建考试
            const examRes = await axios.post('/api/mock-exams/create', {
                examName,
                examTime,
                standardPaperId: selectedPaperId,
                examRoomId: selectedRoomId
            })

            const examId = examRes.data.data
            if (!examId) throw new Error('未获取到创建后的考试ID')

            // 第二步：添加学生
            await axios.post('/api/mock-exams/add-students', {
                examId,
                studentIds: selectedStudentIds
            })

            alert('模拟考试创建并添加学员成功！')
            navigate('/assistant/assistant-exam')
        } catch (err) {
            const msg = err.response?.data?.message || err.message || '未知错误'
            if (msg.includes('该考场当天已有课程/考试安排')) {
                alert('创建失败：考场冲突，请更换时间或教室')
            } else {
                alert('操作失败: ' + msg)
            }
        }
    }

    return (
        <div className="exam-create-wrapper">
            <button className='back-button' onClick={() => navigate(-1)}>返回</button>
            <h2>创建模拟考试</h2>

            <div className="form-group">
                <label>考试名称：</label>
                <input value={examName} onChange={e => setExamName(e.target.value)} />
            </div>

            <div className="form-group">
                <label>考试时间：</label>
                <input type="datetime-local" value={examTime} onChange={e => setExamTime(e.target.value)} />
            </div>

            <div className="form-group">
                <label>选择试卷：</label>
                <select value={selectedPaperId} onChange={e => setSelectedPaperId(e.target.value)}>
                    <option value="">请选择</option>
                    {papers.map(p => (
                        <option key={p.paperId} value={p.paperId}>{p.paperName}</option>
                    ))}
                </select>
            </div>

            <div className="form-group">
                <label>选择考试教室：</label>
                <select value={selectedRoomId} onChange={e => setSelectedRoomId(e.target.value)}>
                    <option value="">请选择</option>
                    {rooms.map(r => (
                        <option key={r.roomId} value={r.roomId}>{r.roomName}</option>
                    ))}
                </select>
            </div>

            <div className="form-group">
                <label>选择参加考试的学员：</label>
                <div className="student-list">
                    {students.map(s => (
                        <label key={s.userId} className="student-item">
                            <input
                                type="checkbox"
                                checked={selectedStudentIds.includes(s.userId)}
                                onChange={() => toggleStudent(s.userId)}
                            />
                            <span>{s.username}（ID: {s.userId}）</span>
                        </label>
                    ))}
                </div>
            </div>

            <div className="form-actions">
                <button onClick={handleCreateExam}>提交考试</button>
            </div>
        </div>
    )
}

export default ExamCreate