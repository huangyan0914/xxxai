<template>
  <div class="qa-container">
    <!-- 左侧会话列表 -->
    <div class="qa-sidebar">
      <n-button class="new-session-btn" type="primary" block @click="handleNewSession">
        + 新建对话
      </n-button>
      <div class="session-list">
        <div
          v-for="session in sessions"
          :key="session.sessionid"
          class="session-item"
          :class="{ active: session.sessionid === currentSessionId }"
          @click="handleSelectSession(session)"
        >
          <span class="session-title">{{ session.title || '新对话' }}</span>
        </div>
      </div>
    </div>

    <!-- 右侧对话区 -->
    <div class="qa-main">
      <!-- 消息列表 -->
      <div ref="messageListRef" class="message-list">
        <template v-if="messages.length === 0 && !currentSessionId">
          <div class="empty-hint">请选择或新建一个对话</div>
        </template>
        <div
          v-for="msg in messages"
          :key="msg.speakId"
          class="message-row"
          :class="msg.role === 'user' ? 'row-user' : 'row-assistant'"
        >
          <!-- 用户消息 -->
          <div v-if="msg.role === 'user'" class="bubble bubble-user">
            <div class="bubble-content">{{ msg.content }}</div>
          </div>

          <!-- AI 消息 -->
          <div v-else class="bubble bubble-assistant">
            <div class="bubble-content" v-html="renderMarkdown(msg.content)"></div>
            <div class="message-actions" v-if="msg.speakId && !msg.isStreaming">
              <n-button
                quaternary
                size="tiny"
                :type="msg.feedback === true ? 'primary' : 'default'"
                @click="handleFeedback(msg, true)"
              >👍</n-button>
              <n-button
                quaternary
                size="tiny"
                :type="msg.feedback === false ? 'error' : 'default'"
                @click="handleFeedback(msg, false)"
              >👎</n-button>
              <n-button quaternary size="tiny" @click="handleShare(msg)">转发</n-button>
              <n-button quaternary size="tiny" title="问题反馈" @click="handleIssueReport(msg)">🔧</n-button>
            </div>
          </div>
        </div>

        <!-- 流式响应占位 -->
        <div v-if="streamingContent !== null" class="message-row row-assistant">
          <div class="bubble bubble-assistant">
            <div class="bubble-content" v-html="renderMarkdown(streamingContent)"></div>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-area">
        <n-input
          v-model:value="inputText"
          type="textarea"
          placeholder="请输入问题，Enter 发送，Shift+Enter 换行"
          :autosize="{ minRows: 2, maxRows: 5 }"
          :disabled="isStreaming"
          @keydown.enter.exact.prevent="handleSend"
          @keydown.enter.shift.exact="() => {}"
        />
        <n-button
          type="primary"
          :loading="isStreaming"
          :disabled="!inputText.trim()"
          @click="handleSend"
          style="margin-top: 8px; align-self: flex-end"
        >
          发送
        </n-button>
      </div>
    </div>

    <!-- 转发弹窗 -->
    <ShareDialog
      v-if="shareDialogVisible"
      :session-id="currentSessionId"
      :session-title="currentSessionTitle"
      @close="shareDialogVisible = false"
    />

    <!-- 问题反馈弹窗 -->
    <IssueReportDialog
      v-if="issueReportDialogVisible"
      :session-id="currentSessionId"
      :speak-id="issueReportTargetMsg?.speakId"
      @close="issueReportDialogVisible = false"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, nextTick, onMounted, computed } from 'vue'
import { useMessage } from 'naive-ui'
import { v4 as uuidv4 } from 'uuid'
import ShareDialog from './ShareDialog.vue'
import IssueReportDialog from './IssueReportDialog.vue'
import { listSessions, getHistory, submitFeedback } from '@/api/intelligentQa'
import { getUserInfo } from '@/utils/auth'

const naiveMessage = useMessage()

// ===== 状态 =====
const sessions = ref<any[]>([])
const currentSessionId = ref<string>('')
const messages = ref<any[]>([])
const inputText = ref('')
const isStreaming = ref(false)
const streamingContent = ref<string | null>(null)
const messageListRef = ref<HTMLElement | null>(null)
const shareDialogVisible = ref(false)
const shareTargetMsg = ref<any>(null)
const issueReportDialogVisible = ref(false)
const issueReportTargetMsg = ref<any>(null)

const currentSessionTitle = computed(() => {
  const s = sessions.value.find((s) => s.sessionId === currentSessionId.value)
  return s?.title || '新对话'
})

// ===== 初始化 =====
onMounted(async () => {
  await loadSessions()
})

async function loadSessions() {
  const res = await listSessions()
  sessions.value = (res as any) || []
}

// ===== 会话操作 =====
function handleNewSession() {
  currentSessionId.value = ''
  messages.value = []
}

async function handleSelectSession(session: any) {
  currentSessionId.value = session.sessionid
  messages.value = []
  const res = await getHistory(session.sessionid)
  messages.value = normalizeHistoryMessages(res as any[])
  scrollToBottom()
}

// 将 LazyCraft 原始历史消息字段映射为前端统一字段
function normalizeHistoryMessages(list: any[]): any[] {
  return (list || []).map((m: any) => ({
    ...m,
    speakId: m.id,
    role: m.from_who === 'lazyllm' ? 'assistant' : 'user',
    feedback: m.is_satisfied !== undefined ? m.is_satisfied : undefined,
    isStreaming: false
  }))
}

// ===== 发送消息 =====
async function handleSend() {
  const text = inputText.value.trim()
  if (!text || isStreaming.value) return

  // 若无 sessionId，生成新 UUID
  if (!currentSessionId.value) {
    currentSessionId.value = uuidv4()
  }

  const userMsg = {
    speakId: uuidv4(),
    role: 'user',
    content: text,
    isStreaming: false
  }
  messages.value.push(userMsg)
  inputText.value = ''
  isStreaming.value = true
  streamingContent.value = ''
  scrollToBottom()

  try {
    const userId = getUserInfo().userId || sessionStorage.getItem('userId') || ''
    const response = await fetch(`/api/qa/sessions/${currentSessionId.value}/messages`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId
      },
      body: JSON.stringify({ sessionId: currentSessionId.value, input: text, files: [] })
    })

    if (!response.ok || !response.body) {
      throw new Error('请求失败')
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      const chunk = decoder.decode(value, { stream: true })
      const lines = chunk.split('\n')
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const jsonStr = line.slice(5).trim()
          if (!jsonStr) continue
          try {
            const evt = JSON.parse(jsonStr)
            // LazyCraft SSE: event=chunk, text in evt.data; event=finish 表示结束
            if (evt.event === 'chunk' && evt.data !== undefined) {
              streamingContent.value = (streamingContent.value || '') + evt.data
              scrollToBottom()
            }
          } catch (_) {
            // ignore non-JSON lines
          }
        }
      }
    }

    const finalContent = streamingContent.value || ''
    streamingContent.value = null

    // 流式结束后重新加载历史，获取带真实 speakId 的消息（用于反馈功能）
    try {
      const historyRes: any = await getHistory(currentSessionId.value)
      const normalized = normalizeHistoryMessages(historyRes as any[])
      if (normalized.length > 0) {
        messages.value = normalized
      } else {
        // 历史暂未落库，降级展示本地消息（无 speakId，不显示反馈按钮）
        messages.value.push({ speakId: undefined, role: 'assistant', content: finalContent, isStreaming: false, feedback: undefined })
      }
    } catch (_) {
      messages.value.push({ speakId: undefined, role: 'assistant', content: finalContent, isStreaming: false, feedback: undefined })
    }

    // 刷新会话列表（可能新建了会话）
    await loadSessions()
    scrollToBottom()
  } catch (e: any) {
    streamingContent.value = null
    naiveMessage.error(e.message || '发送失败')
  } finally {
    isStreaming.value = false
  }
}

// ===== 反馈 =====
async function handleFeedback(msg: any, isSatisfied: boolean) {
  if (msg.feedback === isSatisfied) return
  await submitFeedback({
    sessionId: currentSessionId.value,
    speakId: msg.speakId,
    isSatisfied
  })
  msg.feedback = isSatisfied
  naiveMessage.success(isSatisfied ? '已点赞' : '已点踩')
}

// ===== 转发 =====
function handleShare(msg: any) {
  shareTargetMsg.value = msg
  shareDialogVisible.value = true
}

// ===== 问题反馈 =====
function handleIssueReport(msg: any) {
  issueReportTargetMsg.value = msg
  issueReportDialogVisible.value = true
}

// ===== 工具 =====
function scrollToBottom() {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
  })
}

function renderMarkdown(text: string): string {
  if (!text) return ''
  // 简单转义 HTML，保留换行
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br/>')
}
</script>

<style scoped>
.qa-container {
  display: flex;
  height: 100%;
  background: #f5f5f5;
  overflow: hidden;
}

.qa-sidebar {
  width: 240px;
  min-width: 240px;
  background: #fff;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
  padding: 12px 8px;
  gap: 8px;
}

.new-session-btn {
  flex-shrink: 0;
}

.session-list {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.session-item:hover {
  background: #f0f0f0;
}

.session-item.active {
  background: #e6f4ff;
  color: #1677ff;
}

.qa-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-hint {
  text-align: center;
  color: #999;
  margin-top: 40px;
}

.message-row {
  display: flex;
}

.row-user {
  justify-content: flex-end;
}

.row-assistant {
  justify-content: flex-start;
}

.bubble {
  max-width: 70%;
}

.bubble-user .bubble-content {
  background: #1677ff;
  color: #fff;
  padding: 10px 14px;
  border-radius: 12px 12px 2px 12px;
  word-break: break-word;
  white-space: pre-wrap;
}

.bubble-assistant .bubble-content {
  background: #fff;
  color: #333;
  padding: 10px 14px;
  border-radius: 12px 12px 12px 2px;
  word-break: break-word;
  white-space: pre-wrap;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.message-actions {
  display: flex;
  gap: 4px;
  margin-top: 6px;
  padding-left: 4px;
}

.input-area {
  padding: 12px 24px 16px;
  background: #fff;
  border-top: 1px solid #eee;
  display: flex;
  flex-direction: column;
}
</style>
