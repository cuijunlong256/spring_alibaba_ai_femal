<template>
    <div class="matchmaker-chat-container">
        <!-- 左侧边栏：会话列表 -->
        <div class="sidebar">
            <div class="matchmaker-info">
                <div class="breathing-circle">💝</div>
                <h3 class="assistant-name">{{ assistantName }}</h3>
                <div class="online-status">
                    <div class="status-dot"></div>
                    在线服务中
                </div>
            </div>

            <div class="session-history">
                <div class="section-header">
                    <h4 class="section-title">会话列表</h4>
                    <el-button text type="danger" size="small" @click="createNewSession">
                        <el-icon><Plus /></el-icon>
                        新建
                    </el-button>
                </div>
                <div class="session-list">
                    <div
                        v-for="session in sessionList"
                        :key="session.id"
                        class="session-item"
                        :class="{ active: currentSession?.id === session.id }"
                        @click="handleSessionClick(session)"
                    >
                        <div class="session-info">
                            <div class="session-title">{{ session.sessionTitle }}</div>
                            <div class="session-time">{{ session.startedAt }}</div>
                        </div>
                        <el-button
                            text
                            type="danger"
                            size="small"
                            class="delete-btn"
                            @click.stop="handleDeleteSession(session.id)"
                        >
                            <el-icon><DeleteFilled /></el-icon>
                        </el-button>
                    </div>
                    <div v-if="sessionList.length === 0" class="empty-tip">
                        暂无会话，点击"新建"开始
                    </div>
                </div>
            </div>
        </div>

        <!-- 右侧聊天主区域 -->
        <div class="chat-main">
            <div class="chat-header">
                <div class="header-left">
                    <div class="chat-avatar">💝</div>
                    <div class="chat-info">
                        <h2>{{ assistantName }}</h2>
                        <p>专属{{ genderText }}红娘，为你牵线搭桥</p>
                    </div>
                </div>
                <el-button circle @click="createNewSession" title="新建会话">
                    <el-icon><Plus /></el-icon>
                </el-button>
            </div>

            <!-- 消息区域 -->
            <div class="chat-messages" ref="messagesContainer">
                <div v-for="msg in messages" :key="msg.id" class="message-item" :class="msg.senderType === 1 ? 'user-message' : 'ai-message'">
                    <div class="message-avatar">
                        {{ msg.senderType === 1 ? '😊' : '🤖' }}
                    </div>
                    <div class="message-content">
                        <div class="message-bubble">
                            <div v-if="msg.senderType === 2 && isAiTyping && !msg.content" class="typing-indicator">
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                            </div>
                            <p v-else>{{ msg.content }}</p>
                        </div>
                        <div class="message-time">
                            {{ msg.senderType === 2 && isAiTyping && !msg.content ? '正在输入中...' : formatTime(msg.createdAt) }}
                        </div>
                    </div>
                </div>
            </div>

            <!-- 输入区域 -->
            <div class="chat-input">
                <div v-if="isAiTyping" class="typing-cancel-bar">
                    <span class="typing-text">红娘正在思考中...</span>
                    <el-button size="small" text type="warning" @click="forceUnlockInput">
                        取消等待
                    </el-button>
                </div>
                <div class="input-wrapper">
                    <el-input
                        ref="inputRef"
                        v-model="userMessage"
                        placeholder="说说你喜欢什么类型的对象吧~"
                        type="textarea"
                        :rows="2"
                        :class="{ 'input-disabled-visual': isAiTyping }"
                        @keydown="handleKeyDown"
                        @compositionstart="isComposing = true"
                        @compositionend="handleCompositionEnd"
                        class="message-input"
                    />
                    <el-button
                        :disabled="!userMessage.trim()"
                        type="danger"
                        class="send-btn"
                        @click="sendMessage"
                    >
                        <el-icon><Promotion /></el-icon>
                    </el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, DeleteFilled, Promotion } from '@element-plus/icons-vue'

import {
    startMatchmakerSession,
    getMatchmakerSessionList,
    deleteMatchmakerSession,
    getMatchmakerSessionMessages,
    getMatchmakerStreamUrl
} from '@/api/frontend'

const route = useRoute()

// 性别：male（男用户，推荐女性）/ female（女用户，推荐男性）
const gender = ref(route.query.gender || 'male')

// 根据性别显示文案
const genderText = ref(gender.value === 'male' ? '男性' : '女性')
const assistantName = ref(gender.value === 'male' ? '小红娘' : '小红娘')

// 会话与消息
const currentSession = ref(null)
const sessionList = ref([])
const messages = ref([])
const userMessage = ref('')
const isAiTyping = ref(false)
const inputRef = ref(null)
const messagesContainer = ref(null)

// 输入法状态
const isComposing = ref(false)
let lastCompositionEnd = 0

const handleCompositionEnd = (e) => {
    isComposing.value = false
    lastCompositionEnd = Date.now()
    if (e.target.value !== userMessage.value) {
        userMessage.value = e.target.value
    }
}

const handleKeyDown = (e) => {
    if (isComposing.value && Date.now() - lastCompositionEnd > 2000) {
        isComposing.value = false
    }
    if (e.which === 229 || isComposing.value) return
    if (Date.now() - lastCompositionEnd < 200 && e.key === 'Enter') return
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault()
        sendMessage()
    }
}

watch(userMessage, (newVal, oldVal) => {
    if (oldVal !== newVal && isComposing.value) {
        isComposing.value = false
    }
})

const forceUnlockInput = () => {
    isAiTyping.value = false
    isComposing.value = false
    nextTick(() => {
        inputRef.value?.focus()
        ElMessage.info('已解除锁定')
    })
}

// 新建临时会话
const createNewSession = () => {
    currentSession.value = { id: null, status: 'TEMP', sessionTitle: '新对话' }
    messages.value = []
    isAiTyping.value = false
    userMessage.value = ''
    nextTick(() => inputRef.value?.focus())
}

// 发送消息
const sendMessage = () => {
    if (!userMessage.value.trim()) return
    if (isAiTyping.value) {
        ElMessage.warning('红娘正在回复中，请稍候')
        return
    }

    const message = userMessage.value.trim()
    isComposing.value = false
    userMessage.value = ''

    if (!currentSession.value || currentSession.value.status === 'TEMP') {
        // 临时会话：先创建会话再发送
        startNewSession(message)
    } else {
        messages.value.push({
            id: Date.now(),
            senderType: 1,
            content: message,
            createdAt: new Date().toISOString()
        })
        scrollToBottom()
        startAIResponse(currentSession.value.id, message)
    }
}

// 创建会话并发送首条消息
const startNewSession = (message) => {
    isAiTyping.value = true

    startMatchmakerSession(gender.value, {
        sessionTitle: `情感红娘 - ${new Date().toLocaleString()}`,
        initialMessage: message
    }).then(res => {
        currentSession.value = {
            id: res.sessionId, // 后端返回 session_xxx
            status: 'ACTIVE',
            sessionTitle: res.sessionTitle
        }

        messages.value.push({
            id: `user_${Date.now()}`,
            senderType: 1,
            content: message,
            createdAt: new Date().toISOString()
        })

        // 会话创建成功的欢迎语作为 AI 第一条消息
        messages.value.push({
            id: `ai_${Date.now()}`,
            senderType: 2,
            content: getGreeting(),
            createdAt: new Date().toISOString()
        })

        scrollToBottom()
        getSessionPage()
    }).catch(err => {
        console.error('创建会话失败:', err)
        ElMessage.error('创建会话失败，请稍后重试')
    }).finally(() => {
        isAiTyping.value = false
        nextTick(() => inputRef.value?.focus())
    })
}

// 欢迎语
const getGreeting = () => {
    return gender.value === 'male'
        ? '嗨！我是你的专属红娘，想帮你找到心仪的女生～ 先说说你喜欢什么类型的吧？比如性格、外貌、气质都可以聊～'
        : '嗨！我是你的专属红娘，想帮你找到心仪的男生～ 先说说你喜欢什么类型的吧？比如性格、外貌、气质都可以聊～'
}

// 调用 AI 流式回复
// 调用 AI 流式回复
const startAIResponse = async (sessionId, userMsg) => {
    if (isAiTyping.value) return
    isAiTyping.value = true

    const safetyTimeout = setTimeout(() => {
        if (isAiTyping.value) {
            isAiTyping.value = false
        }
    }, 30000)

    const unlock = () => {
        isAiTyping.value = false
        clearTimeout(safetyTimeout)
        nextTick(() => inputRef.value?.focus())
    }

    const aiMsgId = `ai_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    messages.value.push({
        id: aiMsgId,
        senderType: 2,
        content: '',
        createdAt: new Date().toISOString()
    })
    scrollToBottom()

        let doneReceived = false

        try {
            const response = await fetch(getMatchmakerStreamUrl(gender.value), {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'token': localStorage.getItem('token') || '',
                    'Accept': 'text/event-stream'
                },
                body: JSON.stringify({ sessionId: sessionId, userMessage: userMsg })
            })

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`)
            }

            const reader = response.body.getReader()
            const decoder = new TextDecoder()
            let buffer = ''

            const processChunk = (chunk) => {
                buffer += decoder.decode(chunk, { stream: true })
                const lines = buffer.split('\n')
                buffer = lines.pop() || ''

                let eventName = ''
                for (const line of lines) {
                    if (line.startsWith('event:')) {
                        eventName = line.substring(6).trim()
                    } else if (line.startsWith('data:')) {
                        const raw = line.substring(5).trim()
                        const aiMessage = messages.value.find(m => m.id === aiMsgId)
                        if (!aiMessage) continue

                        if (eventName === 'done') {
                            doneReceived = true
                            unlock()
                            return
                        }
                        if (eventName === 'error') {
                            aiMessage.content = '回复出错了，请重试'
                            unlock()
                            return
                        }

                        try {
                            const payload = JSON.parse(raw)
                            if (payload.code == 200 && payload.data?.content) {
                                aiMessage.content += payload.data.content
                                scrollToBottom()
                            }
                        } catch (e) {
                            console.warn('chunk解析失败:', e)
                        }
                    }
                }
            }

            while (true) {
                const { done, value } = await reader.read()
                if (done) break
                processChunk(value)
            }

            if (!doneReceived) unlock()
        } catch (err) {
            console.error('SSE error:', err)
            const aiMessage = messages.value.find(m => m.id === aiMsgId)
            if (aiMessage && !aiMessage.content) {
                aiMessage.content = '回复失败，请重试'
            }
            unlock()
        }

}

// 获取会话列表
const getSessionPage = () => {
    getMatchmakerSessionList(gender.value, { pageNum: 1, pageSize: 50 }).then(res => {
        sessionList.value = res.records || []
    }).catch(err => {
        console.error('获取会话列表失败:', err)
    })
}

// 点击会话切换
const handleSessionClick = (session) => {
    const sessionId = session.id
    currentSession.value = { id: sessionId, status: 'ACTIVE', sessionTitle: session.sessionTitle }

    getMatchmakerSessionMessages(gender.value, sessionId).then(res => {
        // 后端返回 List<ConsultationMessage>
        const list = Array.isArray(res) ? res : (res.data || [])
        messages.value = list.map(m => ({
            id: m.id,
            senderType: m.senderType,
            content: m.content,
            createdAt: m.createdAt
        }))
        scrollToBottom()
    }).catch(err => {
        console.error('加载消息失败:', err)
        ElMessage.error('加载消息失败')
    })
}

// 删除会话
const handleDeleteSession = (sessionId) => {
    deleteMatchmakerSession(gender.value, sessionId).then(() => {
        ElMessage.success('删除成功')
        if (currentSession.value?.id === sessionId) {
            createNewSession()
        }
        getSessionPage()
    }).catch(err => {
        console.error('删除失败:', err)
        ElMessage.error('删除失败')
    })
}

// 滚动到底部
const scrollToBottom = () => {
    nextTick(() => {
        if (messagesContainer.value) {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
    })
}

// 时间格式化
const formatTime = (time) => {
    if (!time) return ''
    const d = new Date(time)
    if (isNaN(d.getTime())) return time
    const pad = (n) => n.toString().padStart(2, '0')
    return `${pad(d.getHours())}:${pad(d.getMinutes())}`
}

onMounted(() => {
    getSessionPage()
    createNewSession()
})
</script>

<style scoped lang="scss">
.matchmaker-chat-container {
    margin: 0 auto;
    width: 1200px;
    display: flex;
    gap: 20px;
    padding: 20px;

    .sidebar {
        width: 300px;
        flex-shrink: 0;

        .matchmaker-info {
            margin-bottom: 20px;
            background: linear-gradient(135deg, #fff0f5 0%, #ffe4ec 100%);
            border-radius: 16px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 4px 16px rgba(255, 107, 157, 0.1);

            .breathing-circle {
                width: 60px;
                height: 60px;
                margin: 0 auto 12px;
                background: linear-gradient(135deg, #ff6b9d 0%, #ffa8c5 100%);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 28px;
                animation: breathing 4s ease-in-out infinite;
                box-shadow: 0 6px 24px rgba(255, 107, 157, 0.25);
            }
            .assistant-name {
                font-size: 18px;
                font-weight: 700;
                color: #d63384;
                margin: 0 0 8px;
            }
            .online-status {
                display: flex;
                align-items: center;
                justify-content: center;
                color: #059669;
                font-size: 12px;
                font-weight: 600;
                .status-dot {
                    width: 8px;
                    height: 8px;
                    background: #059669;
                    border-radius: 50%;
                    margin-right: 6px;
                    animation: pulse 2s infinite;
                }
            }
        }

        .session-history {
            background: white;
            border-radius: 16px;
            padding: 16px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

            .section-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 12px;
                .section-title {
                    font-size: 16px;
                    font-weight: 600;
                    color: #333;
                    margin: 0;
                }
            }

            .session-list {
                max-height: 480px;
                overflow-y: auto;

                .session-item {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    padding: 12px;
                    margin-bottom: 8px;
                    border-radius: 12px;
                    cursor: pointer;
                    border: 2px solid transparent;
                    transition: all 0.2s;
                    &:hover {
                        background: #fff5f8;
                    }
                    &.active {
                        background: #fff0f5;
                        border-color: #ff6b9d;
                    }
                    .session-info {
                        flex: 1;
                        min-width: 0;
                        .session-title {
                            font-size: 14px;
                            font-weight: 500;
                            color: #333;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            margin-bottom: 4px;
                        }
                        .session-time {
                            font-size: 12px;
                            color: #999;
                        }
                    }
                    .delete-btn {
                        opacity: 0;
                        transition: opacity 0.2s;
                    }
                    &:hover .delete-btn {
                        opacity: 1;
                    }
                }
            }
            .empty-tip {
                text-align: center;
                padding: 30px 10px;
                color: #bbb;
                font-size: 13px;
            }
        }
    }

    .chat-main {
        background: white;
        border-radius: 20px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
        display: flex;
        flex-direction: column;
        overflow: hidden;
        flex: 1;

        .chat-header {
            background: linear-gradient(135deg, #ff6b9d 0%, #ffa8c5 100%);
            color: white;
            padding: 18px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;

            .header-left {
                display: flex;
                align-items: center;
                .chat-avatar {
                    width: 44px;
                    height: 44px;
                    background: rgba(255, 255, 255, 0.25);
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 22px;
                    margin-right: 14px;
                }
                .chat-info {
                    h2 { font-size: 18px; font-weight: 700; margin: 0 0 2px; }
                    p { font-size: 13px; opacity: 0.9; margin: 0; }
                }
            }
        }

        .chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 24px;
            display: flex;
            flex-direction: column;
            gap: 16px;
            max-height: calc(100vh - 260px);

            .message-item {
                display: flex;
                gap: 10px;
                align-items: flex-start;

                .message-avatar {
                    width: 32px;
                    height: 32px;
                    border-radius: 50%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-size: 16px;
                    flex-shrink: 0;
                }
                &.ai-message .message-avatar { background: #ffe4ec; }
                &.user-message .message-avatar { background: #f0f0f0; }

                .message-content {
                    max-width: 70%;
                    .message-bubble {
                        padding: 12px 16px;
                        border-radius: 14px;
                        font-size: 14px;
                        line-height: 1.6;
                        .typing-indicator {
                            display: flex; gap: 4px; padding: 4px 0;
                            .typing-dot {
                                width: 8px; height: 8px; background: #ff9ebd; border-radius: 50%;
                                animation: typing 1.5s infinite;
                                &:nth-child(2) { animation-delay: 0.2s; }
                                &:nth-child(3) { animation-delay: 0.4s; }
                            }
                        }
                        p { margin: 0; white-space: pre-wrap; word-break: break-word; }
                    }
                    &.ai-message .message-bubble { background: #fff0f5; color: #444; }
                    &.user-message .message-bubble { background: #ff6b9d; color: white; }
                    .message-time { font-size: 11px; color: #bbb; margin-top: 4px; }
                }

                &.user-message {
                    flex-direction: row-reverse;
                    .message-content { text-align: right; }
                }
            }
        }

        .chat-input {
            border-top: 1px solid #f0f0f0;
            padding: 16px 24px;
            background: #fafafa;

            .typing-cancel-bar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 8px 12px;
                margin-bottom: 10px;
                background: #fff0f5;
                border-radius: 8px;
                font-size: 13px;
                color: #d63384;
            }
            .input-wrapper {
                display: flex;
                gap: 12px;
                align-items: flex-end;
            }
            .message-input {
                flex: 1;
            }
            .input-disabled-visual { opacity: 0.6; }
            .send-btn {
                height: 56px;
                width: 56px;
                border-radius: 14px;
                background: linear-gradient(135deg, #ff6b9d 0%, #ffa8c5 100%) !important;
                border: none !important;
            }
        }
    }
}

@keyframes breathing {
    0%, 100% { transform: scale(1); }
    50% { transform: scale(1.08); }
}
@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
}
@keyframes typing {
    0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
    30% { transform: translateY(-6px); opacity: 1; }
}
</style>