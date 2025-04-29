// AdminResourceCategory.jsx
import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import './admin_resource_category.css'

const AdminResourceCategory = () => {
    const navigate = useNavigate()
    const [categories, setCategories] = useState([])
    const [newCategory, setNewCategory] = useState({ categoryName: '', description: '' })
    const [editMode, setEditMode] = useState(false)
    const [editingCategoryId, setEditingCategoryId] = useState(null)
    const [modalVisible, setModalVisible] = useState(false)

    useEffect(() => {
        fetchCategories()
    }, [])

    const fetchCategories = async () => {
        try {
            const res = await axios.get('/api/resource-categories')
            setCategories(res.data.data || [])
        } catch (err) {
            alert('获取资源类目失败: ' + (err.response?.data?.message || '未知错误'))
        }
    }

    const handleAddCategory = async () => {
        if (!newCategory.categoryName.trim()) {
            alert('请输入类目名称')
            return
        }
        try {
            await axios.post('/api/resource-categories', newCategory)
            alert('添加成功')
            setNewCategory({ categoryName: '', description: '' })
            fetchCategories()
        } catch (err) {
            alert('添加失败: ' + (err.response?.data?.message || '未知错误'))
        }
    }

    const handleUpdateCategory = async () => {
        if (!newCategory.categoryName.trim()) {
            alert('请输入类目名称')
            return
        }
        try {
            await axios.put(`/api/resource-categories/${editingCategoryId}`, newCategory)
            alert('修改成功')
            setNewCategory({ categoryName: '', description: '' })
            setEditMode(false)
            setEditingCategoryId(null)
            setModalVisible(false) // ⭐️ 关闭弹窗
            fetchCategories()
        } catch (err) {
            alert('修改失败: ' + (err.response?.data?.message || '未知错误'))
        }
    }

    const handleDeleteCategory = async (categoryId) => {
        if (!window.confirm('确定要删除该资源类目吗？')) return
        try {
            const res = await axios.delete(`/api/resource-categories/${categoryId}`)
            if (res.data.code === 200) {
                alert('删除成功')
                fetchCategories()
            } else {
                alert('删除失败: ' + (res.data.message || '未知错误'))
            }
        } catch (err) {
            alert('删除失败: ' + (err.response?.data?.message || '未知错误'))
        }
    }

    const startEdit = (category) => {
        setNewCategory({ categoryName: category.categoryName, description: category.description || '' })
        setEditingCategoryId(category.categoryId)
        setEditMode(true)
        setModalVisible(true) // ⭐️ 打开弹窗
    }

    const cancelEdit = () => {
        setNewCategory({ categoryName: '', description: '' })
        setEditMode(false)
        setEditingCategoryId(null)
        setModalVisible(false) // ⭐️ 关闭弹窗
    }

    return (
        <div className="resource-category-wrapper">
            <button className="back-button" onClick={() => navigate('/admin/admin-resource')}>
                返回
            </button>
            <h2 className="title">资源类目管理</h2>

            <div className="resource-category-actions">
                <input
                    type="text"
                    placeholder="新类目名称"
                    value={newCategory.categoryName}
                    onChange={(e) => setNewCategory({ ...newCategory, categoryName: e.target.value })}
                />
                <input
                    type="text"
                    placeholder="新类目描述（可选）"
                    value={newCategory.description}
                    onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })}
                />
                <button className="add-button" onClick={handleAddCategory}>添加资源类目</button>
            </div>

            <div className="resource-category-list">
                {categories.length > 0 ? (
                    <table className="category-table">
                        <thead>
                            <tr>
                                <th>类目名称</th>
                                <th>描述</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            {categories.map((cat) => (
                                <tr key={cat.categoryId}>
                                    <td>{cat.categoryName}</td>
                                    <td>{cat.description || '-'}</td>
                                    <td>
                                        <button className="edit-button" onClick={() => startEdit(cat)}>编辑</button>
                                        <button className="delete-button" onClick={() => handleDeleteCategory(cat.categoryId)}>删除</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <p className="no-data-text">暂无资源类目</p>
                )}
            </div>

            {/* 编辑弹窗 */}
            {modalVisible && (
                <div className="resource-category-modal-overlay">
                    <div className="resource-category-modal">
                        <h3>编辑资源类目</h3>
                        <input
                            type="text"
                            placeholder="类目名称"
                            value={newCategory.categoryName}
                            onChange={(e) => setNewCategory({ ...newCategory, categoryName: e.target.value })}
                        />
                        <input
                            type="text"
                            placeholder="描述（可选）"
                            value={newCategory.description}
                            onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })}
                        />
                        <div className="resource-category-modal-buttons">
                            <button className="save-button" onClick={handleUpdateCategory}>保存修改</button>
                            <button className="cancel-button" onClick={cancelEdit}>取消</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default AdminResourceCategory