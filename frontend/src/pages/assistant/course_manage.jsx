import React, { useEffect, useState } from 'react'
import axios from 'axios'
import './course_manage.css'
import { useNavigate } from 'react-router-dom'

const CourseManage = () => {
    const navigate = useNavigate()
    const [courses, setCourses] = useState([])
    const [showModal, setShowModal] = useState(false)
    const [editMode, setEditMode] = useState(false)
    const [currentId, setCurrentId] = useState(null)
    const [form, setForm] = useState({
        courseName: '',
        courseType: '班级',
        courseContent: '',
        classGroupCode: '',
        totalHours: '',
        studentId: ''
    })
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')

    useEffect(() => {
        fetchCourses()
    }, [])

    const fetchCourses = async () => {
        try {
            const res = await axios.get('/api/courses/all')
            setCourses(res.data.data)
        } catch (err) {
            console.error('获取课程失败', err)
        }
    }

    const openCreateModal = () => {
        setEditMode(false)
        setForm({
            courseName: '',
            courseType: '班级',
            courseContent: '',
            classGroupCode: '',
            totalHours: '',
            studentId: ''
        })
        setShowModal(true)
    }

    const openEditModal = (course) => {
        setEditMode(true)
        setCurrentId(course.courseId)
        setForm({
            courseName: course.courseName,
            courseType: course.courseType,
            courseContent: course.courseContent || '',
            classGroupCode: course.classGroupCode || '',
            totalHours: course.totalHours || '',
            studentId: course.studentId || course.student?.userId || ''
        })
        setShowModal(true)
    }

    const handleInputChange = (e) => {
        const { name, value } = e.target
        setForm(prev => ({ ...prev, [name]: value }))
    }

    const handleSubmit = async () => {
        setError('')
        setSuccess('')
        try {
            if (editMode) {
                await axios.put('/api/courses/update', {
                    courseId: currentId,
                    ...form,
                    totalHours: Number(form.totalHours),
                    studentId: form.courseType === '1对1' ? Number(form.studentId) : null
                })
                setSuccess('修改成功')
            } else {
                await axios.post('/api/courses/create', {
                    ...form,
                    totalHours: Number(form.totalHours),
                    studentId: form.courseType === '1对1' ? Number(form.studentId) : null
                })
                setSuccess('创建成功')
            }
            setShowModal(false)
            fetchCourses()
        } catch (err) {
            setError(err.response?.data?.message || '操作失败')
        }
    }

    const handleDelete = async (id) => {
        if (!window.confirm('确认删除该课程？')) return
        try {
            await axios.delete(`/api/courses/${id}`)
            fetchCourses()
        } catch (err) {
            alert(err.response?.data?.message || '删除失败')
        }
    }

    return (
        <div className="course-manage-wrapper">
            <button className="back-button" onClick={() => navigate('/assistant/assistant-course')}>
                返回
            </button>
            <h2 className='title'>课程管理</h2>
            <button className="create-button" onClick={openCreateModal}>+ 创建课程</button>

            <table className="course-table">
                <thead>
                    <tr>
                        <th>课程ID</th>
                        <th>名称</th>
                        <th>类型</th>
                        <th>内容</th>
                        <th>总课时</th>
                        <th>班级</th>
                        <th>1对1学生</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    {courses.map(course => (
                        <tr key={course.courseId}>
                            <td>{course.courseId}</td>
                            <td>{course.courseName}</td>
                            <td>{course.courseType}</td>
                            <td>{course.courseContent}</td>
                            <td>{course.totalHours}</td>
                            <td>{course.classGroupCode || '-'}</td>
                            <td>{course.student?.username || course.studentName || '-'}</td>
                            <td>
                                <button className="edit" onClick={() => openEditModal(course)}>编辑</button>
                                <button className="delete" onClick={() => handleDelete(course.courseId)}>删除</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

            {showModal && (
                <div className="modal-overlay">
                    <div className="modal_course">
                        <h3>{editMode ? '编辑课程' : '创建课程'}</h3>

                        <input
                            name="courseName"
                            placeholder="课程名称"
                            value={form.courseName}
                            onChange={handleInputChange}
                        />

                        <select
                            name="courseType"
                            value={form.courseType}
                            onChange={handleInputChange}
                        >
                            <option value="班级">班级</option>
                            <option value="1对1">1对1</option>
                        </select>

                        <input
                            name="courseContent"
                            placeholder="课程内容"
                            value={form.courseContent}
                            onChange={handleInputChange}
                        />

                        {/* ✅ 只有在选择“班级”时，才显示班级编号输入框 */}
                        {form.courseType === '班级' && (
                            <input
                                name="classGroupCode"
                                placeholder="班级编号（班级课）"
                                value={form.classGroupCode}
                                onChange={handleInputChange}
                            />
                        )}

                        <input
                            name="totalHours"
                            type="number"
                            placeholder="总课时"
                            value={form.totalHours}
                            onChange={handleInputChange}
                        />

                        {/* ✅ 只有在选择“1对1”时，才显示学生ID输入框 */}
                        {form.courseType === '1对1' && (
                            <input
                                name="studentId"
                                placeholder="1对1学生ID"
                                value={form.studentId}
                                onChange={handleInputChange}
                            />
                        )}

                        {error && <p className="error">{error}</p>}
                        {success && <p className="success">{success}</p>}

                        <div className="modal-buttons">
                            <button onClick={handleSubmit}>提交</button>
                            <button onClick={() => setShowModal(false)}>取消</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default CourseManage