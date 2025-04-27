import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './admin_resource_audit.css';
import dayjs from 'dayjs';

const AdminResourceAudit = () => {
    const navigate = useNavigate();
    const [resources, setResources] = useState([]);

    useEffect(() => {
        fetchPendingResources();
    }, []);

    const fetchPendingResources = async () => {
        try {
            const res = await axios.get('/api/resource-categories/pending');
            if (res.data && res.data.data) {
                setResources(res.data.data);
            } else {
                setResources([]);
            }
        } catch (err) {
            alert('获取待审核资源失败: ' + (err.response?.data?.message || '未知错误'));
        }
    };

    const handleAudit = async (resourceId, approved) => {
        try {
            await axios.post('/api/resource-categories/audit', {
                resourceId,
                approved
            });
            alert('审核成功');
            fetchPendingResources();
        } catch (err) {
            alert('审核失败: ' + (err.response?.data?.message || '未知错误'));
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return '';
        return dayjs(dateStr).format('YYYY-MM-DD HH:mm');
    };

    return (
        <div className="admin-resource-audit-wrapper">
            <button className="back-button" onClick={() => navigate('/admin/admin-resource')}>
                返回
            </button>

            <h2 className="title">资源审核发布</h2>

            <div className="admin-resource-audit-content">
                {resources.length > 0 ? (
                    <table className="resource-audit-table">
                        <thead>
                            <tr>
                                <th>资源ID</th>
                                <th>资源名称</th>
                                <th>类型</th>
                                <th>分类</th>
                                <th>上传人</th>
                                <th>上传时间</th>
                                <th>审核状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {resources.map((r) => (
                                <tr key={r.resourceId}>
                                    <td>{r.resourceId}</td>
                                    <td>{r.resourceName}</td>
                                    <td>{r.resourceType}</td>
                                    <td>{r.categoryName}</td>
                                    <td>{r.uploaderName}</td>
                                    <td>{formatDate(r.uploadTime)}</td>
                                    <td>{r.auditStatus}</td>
                                    <td>
                                        <button
                                            className="audit-button approve"
                                            onClick={() => handleAudit(r.resourceId, true)}
                                        >
                                            通过
                                        </button>
                                        <button
                                            className="audit-button reject"
                                            onClick={() => handleAudit(r.resourceId, false)}
                                        >
                                            驳回
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p className="no-data-text">暂无待审核资源</p>
                )}
            </div>
        </div>
    );
};

export default AdminResourceAudit;