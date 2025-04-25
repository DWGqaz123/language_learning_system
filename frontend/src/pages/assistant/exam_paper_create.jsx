import React, { useState } from 'react'
import './exam_paper_create.css'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const ExamPaperCreate = () => {
  const navigate = useNavigate()
  const [paperName, setPaperName] = useState('')
  const [examType, setExamType] = useState('模拟考试')
  const [mode, setMode] = useState('structured') // structured | json
  const [questions, setQuestions] = useState([])
  const [jsonInput, setJsonInput] = useState('')
  const [jsonAnswerInput, setJsonAnswerInput] = useState('')

  const addQuestion = () => {
    setQuestions([
      ...questions,
      {
        questionText: '',
        type: 'objective',
        optionA: '',
        optionB: '',
        optionC: '',
        optionD: '',
        correctAnswer: '',
        score: ''
      }
    ])
  }

  const updateQuestion = (index, key, value) => {
    const updated = [...questions]
    updated[index][key] = value
    setQuestions(updated)
  }

  const submitPaper = async () => {
    try {
      const payload = {
        paperName,
        examType,
        paperContentJson: mode === 'json' ? jsonInput : JSON.stringify(questions),
        objectiveAnswersJson: mode === 'json'
          ? jsonAnswerInput
          : JSON.stringify(
              questions
                .filter((q) => q.type === 'objective')
                .map((q, index) => ({
                  questionId: `Q${index + 1}`,
                  answer: q.correctAnswer
                }))
            )
      }
      await axios.post('/api/exams/papers/create', payload)
      alert('试卷录入成功！')
      setPaperName('')
      setExamType('模拟考试')
      setQuestions([])
      setJsonInput('')
      setJsonAnswerInput('')
    } catch (err) {
      alert('提交失败: ' + (err.response?.data?.message || '未知错误'))
    }
  }

  return (
    <div className="exam-create-wrapper">
      <button className="back-button" onClick={() => navigate('/assistant/assistant-exam')}>返回</button>
      <h2>录入标准试卷</h2>

      <div className="form-group">
        <label>试卷名称：</label>
        <input value={paperName} onChange={e => setPaperName(e.target.value)} />
      </div>

      <div className="form-group">
        <label>考试类型：</label>
        <input
          type="text"
          value={examType}
          onChange={(e) => setExamType(e.target.value)}
          placeholder="请输入考试类型，如 模拟考试、期中测试等"
        />
      </div>

      <div className="mode-switch">
        <button onClick={() => setMode('structured')} className={mode === 'structured' ? 'active' : ''}>结构化逐题录入</button>
        <button onClick={() => setMode('json')} className={mode === 'json' ? 'active' : ''}>整段JSON录入</button>
      </div>

      {mode === 'structured' ? (
        <div className="structured-section">
          <button onClick={addQuestion}>添加题目</button>
          {questions.map((q, idx) => (
            <div className="question-card" key={idx}>
              <h4>题目 {idx + 1}</h4>
              <input placeholder="题干" value={q.questionText} onChange={e => updateQuestion(idx, 'questionText', e.target.value)} />
              <select value={q.type} onChange={e => updateQuestion(idx, 'type', e.target.value)}>
                <option value="objective">客观题</option>
                <option value="subjective">主观题</option>
              </select>
              {q.type === 'objective' && (
                <>
                  <input placeholder="A" value={q.optionA} onChange={e => updateQuestion(idx, 'optionA', e.target.value)} />
                  <input placeholder="B" value={q.optionB} onChange={e => updateQuestion(idx, 'optionB', e.target.value)} />
                  <input placeholder="C" value={q.optionC} onChange={e => updateQuestion(idx, 'optionC', e.target.value)} />
                  <input placeholder="D" value={q.optionD} onChange={e => updateQuestion(idx, 'optionD', e.target.value)} />
                  <input placeholder="正确答案（如A）" value={q.correctAnswer} onChange={e => updateQuestion(idx, 'correctAnswer', e.target.value)} />
                </>
              )}
              <input placeholder="分数" type="number" value={q.score} onChange={e => updateQuestion(idx, 'score', e.target.value)} />
            </div>
          ))}
        </div>
      ) : (
        <div className="json-section">
          <h4>输入完整的试卷内容 JSON：</h4>
          <textarea
            rows={10}
            value={jsonInput}
            onChange={e => setJsonInput(e.target.value)}
            placeholder='[{"questionText": "xxx", "type": "objective", "optionA": "...", "score": 5}, ...]'
          />
          <h4>输入客观题答案 JSON：</h4>
          <textarea
            rows={6}
            value={jsonAnswerInput}
            onChange={e => setJsonAnswerInput(e.target.value)}
            placeholder='[{"questionId": "Q1", "answer": "A"}, {"questionId": "Q2", "answer": "C"}]'
          />
        </div>
      )}

      <div className="form-actions">
        <button onClick={submitPaper}>提交试卷</button>
      </div>
    </div>
  )
}

export default ExamPaperCreate