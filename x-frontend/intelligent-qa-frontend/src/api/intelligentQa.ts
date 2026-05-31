import { http } from '@/utils/http/axios'

// ===== 会话列表 =====
export function listSessions() {
  return http.request<any[]>({ url: '/api/qa/sessions', method: 'get' })
}

// ===== 会话历史消息 =====
export function getHistory(sessionId: string) {
  return http.request<any[]>({ url: `/api/qa/sessions/${sessionId}/messages`, method: 'get' })
}

// ===== 提交消息反馈（点赞/点踩）=====
export function submitFeedback(data: {
  sessionId: string
  speakId: number
  isSatisfied: boolean
  userFeedback?: string
}) {
  return http.request({ url: '/api/qa/messages/feedback', method: 'post', data })
}

// ===== 提交问题反馈（存疑/错误）=====
export function submitIssueReport(data: {
  sessionId: string
  speakId: number
  doubtIssue?: string
  wrongAnswer?: string
  correctAnswer?: string
}) {
  return http.request({ url: '/api/qa/messages/report', method: 'post', data })
}
