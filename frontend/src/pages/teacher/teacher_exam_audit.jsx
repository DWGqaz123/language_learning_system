import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './teacher_exam_audit.css';
import { useNavigate } from 'react-router-dom';
const TeacherExamAudit = () => {
    const [pendingPapers, setPendingPapers] = useState([]);
    const [selectedPaper, setSelectedPaper] = useState(null);
    const [auditComment, setAuditComment] = useState('');
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();
    const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}');
    const teacherId = storedUser.userId;

    useEffect(() => {
        fetchPendingPapers();
    }, []);

    const fetchPendingPapers = async () => {
        try {
            const res = await axios.get('/api/exams/papers/pending-papers');
            setPendingPapers(res.data.data || []);
        } catch (err) {
            alert('获取待审核试卷失败: ' + (err.response?.data?.message || '未知错误'));
        }
    };

    const openAuditModal = async (paperId) => {
        try {
            const res = await axios.get(`/api/exams/papers/${paperId}`);
            const paper = res.data.data;
            const questions = paper.paperContentJson ? JSON.parse(paper.paperContentJson) : [];
            setSelectedPaper({ ...paper, questions });
            setAuditComment('');
            setShowModal(true);
        } catch (err) {
            alert('获取试卷详情失败: ' + (err.response?.data?.message || '未知错误'));
        }
    };

    const submitAudit = async (approved) => {
        if (!selectedPaper) return;
        try {
            await axios.post('/api/exams/papers/audit', {
                paperId: selectedPaper.paperId,
                auditStatus: approved ? '通过' : '驳回',
                auditComment: auditComment,
                teacherId: teacherId
            });
            alert('审核完成');
            setShowModal(false);
            fetchPendingPapers();
        } catch (err) {
            alert('审核失败: ' + (err.response?.data?.message || '未知错误'));
        }
    };

    const formatTime = (timeStr) => {
        const date = new Date(timeStr);
        return date.toLocaleString();
    };

    return (
        <div className="audit-wrapper">
            <button className="back-button" onClick={() => navigate('/teacher/teacher-exam')}>返回</button>
            <h2 className='title'>待审核试卷列表</h2>
            <div className="paper-list">
                {pendingPapers.length > 0 ? (
                    pendingPapers.map(paper => (
                        <div key={paper.paperId} className="paper-card">
                            <h4>{paper.paperName}</h4>
                            <p>考试类型：{paper.examType}</p>
                            <p>发布时间：{formatTime(paper.createdTime)}</p>
                            <p>状态：{paper.auditStatus || '待审核'}</p>
                            <button onClick={() => openAuditModal(paper.paperId)}>审核试卷</button>
                        </div>
                    ))
                ) : (
                    <p>暂无待审核试卷</p>
                )}
            </div>

            {showModal && selectedPaper && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <h3>试卷详情</h3>
                        <div className="paper-meta">
                            <p><strong>试卷名称：</strong>{selectedPaper.paperName}</p>
                            <p><strong>考试类型：</strong>{selectedPaper.examType}</p>
                            <p><strong>创建时间：</strong>{formatTime(selectedPaper.createdTime)}</p>
                            <p><strong>审核状态：</strong>{selectedPaper.auditStatus || '未审核'}</p>
                            {selectedPaper.auditedByName && (
                                <p><strong>审核人：</strong>{selectedPaper.auditedByName}</p>
                            )}
                            {selectedPaper.auditComment && (
                                <p><strong>审核意见：</strong>{selectedPaper.auditComment}</p>
                            )}
                        </div>

                        <div className="question-list">
                            {selectedPaper.questions.length > 0 ? (
                                selectedPaper.questions.map((q, idx) => (
                                    <div key={idx} className="question-item">
                                        <p><strong>题目 {idx + 1}：</strong>{q.questionText}</p>
                                        {q.type === 'objective' && (
                                            <ul>
                                                <li>A. {q.optionA}</li>
                                                <li>B. {q.optionB}</li>
                                                <li>C. {q.optionC}</li>
                                                <li>D. {q.optionD}</li>
                                            </ul>
                                        )}
                                        <p><strong>参考答案：</strong>{q.correctAnswer || '主观题无标准答案'}</p>
                                        <p><strong>分值：</strong>{q.score} 分</p>
                                    </div>
                                ))
                            ) : (
                                <p>暂无题目信息</p>
                            )}
                        </div>

                        <textarea
                            placeholder="请输入审核意见"
                            value={auditComment}
                            onChange={(e) => setAuditComment(e.target.value)}
                        />
                        <div className="modal-buttons">
                            <button className="approve" onClick={() => submitAudit(true)}>审核通过</button>
                            <button className="reject" onClick={() => submitAudit(false)}>驳回</button>
                            <button onClick={() => setShowModal(false)}>关闭</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TeacherExamAudit;
