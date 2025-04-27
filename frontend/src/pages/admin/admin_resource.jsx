import React from 'react'
import { Link } from 'react-router-dom'
import './admin.css'

const AdminResource = () => {
    const storedUser = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

    return (
        <div className="admin-layout">
            <aside className="sider">
                <div className="avatar">
                    <div className="circle">管</div>
                    <div className="label">管理员: {storedUser.username || '...'}</div>
                    <div className="label">用户ID: {storedUser.userId || '...'}</div>
                    <div className="label">账号: {storedUser.phoneNumber || '...'}</div>
                </div>
                <ul className="menu">
                    <li><Link to="/admin/admin-user">用户管理</Link></li>
                    <li><Link to="/admin/admin-study-room">自习室管理</Link></li>
                    <li className="active"><Link to="/admin/admin-resource">资源管理</Link></li>
                    <li><Link to="/" onClick={() => sessionStorage.clear()}>退出系统</Link></li>
                </ul>
            </aside>

            <main className="main">
                <header className="header">
                    <h2>语言学习机构后台管理系统</h2>
                    <h2>欢迎您，{storedUser.username || '管理员'}！这是资源管理模块。</h2>
                </header>

                <section className="content">
                    <div className="card-grid">
                        <div className="card">
                            <h3>管理资源类目</h3>
                            <p>对教学资源的分类进行添加、编辑、删除操作。</p>
                            <Link to="/admin/admin-resource-category">立即进入</Link>
                        </div>

                        <div className="card">
                            <h3>管理资源列表</h3>
                            <p>查看、编辑、删除已发布的教学资源。</p>
                            <Link to="/admin/admin-resource-list">立即进入</Link>
                        </div>

                        <div className="card">
                            <h3>审核资源发布</h3>
                            <p>审核助教和教师上传的资源，决定是否发布。</p>
                            <Link to="/admin/admin-resource-audit">立即进入</Link>
                        </div>
                    </div>
                </section>
            </main>
        </div>
    )
}

export default AdminResource