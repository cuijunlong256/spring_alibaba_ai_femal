<template>
    <div class="consultation-container">
        <div class="sidebar">
            <!-- AI助手信息 -->
             <div class="ai-assistant-info">
                <div class="breathing-circle">
                    <el-image :src="iconUrl" style="width: 25px;height:25px" alt="AI助手" />
                </div>
                <h3 class="assistant-name">宁渡AI助手</h3>
                <div class="online-status">
                    <div class="status-dot"></div>
                    在线服务中
                </div>
             </div>
             <!-- 情绪花园 -->
             <div class="emotion-garden">
                <div class="garden-header">
                    <div class="garden-title"> 情绪花园 </div>
                </div>
                <div class="emotion-info">
                    <div class="emotion-name">{{ currentEmotion.primaryEmotion }}</div>
                    <div class="emotion-score">{{ currentEmotion.emotionScore }}</div>
                </div>
                <div class="warm-tips">
                    <div class="emotion-status-text">
                        <span class="status-label">今天感觉</span>
                        <span class="status-emotion">{{ currentEmotion.isNegative ? '需要关注' : '很不错' }}</span>
                    </div>
                    <div class="emotion-intensity">
                        <span class="intensity-dots">
                            <span v-for="dot in 3" :key="dot" class="dot" :class="{'active': getIntensityClass(currentEmotion.emotionScore || 0) >= dot}"></span>
                        </span>
                        <span class="intensity-text">
                            {{ getRiskText(currentEmotion.riskLevel) }}
                        </span>
                    </div>
                     <div class="warm-suggestion" v-if="currentEmotion.suggestion">
                        <div class="suggestion-icon">💝</div>
                        <div class="suggestion-content">
                            <div class="suggestion-title">给你的小建议</div>
                            <div class="suggestion-text">{{ currentEmotion.suggestion }}</div>
                        </div>
                     </div>
                      <div class="healing-actions" v-if="(currentEmotion.improvementSuggestions?.length || 0) > 0">
                        <div class="actions-title">治愈小行动</div>
                        <div class="actions-list">
                            <div v-for="action in currentEmotion.improvementSuggestions" :key="action" class="action-item">
                                <div class="action-icon">✨</div>
                                <div class="action-text">{{ action }}</div>
                            </div>
                        </div>
                      </div>
                    <div class="risk-notice" v-if="currentEmotion.isNegative && currentEmotion.riskLevel > 1">
                        <div class="notice-icon">🤗</div>
                        <div class="notice-content">
                            <div class="notice-title">温馨提示</div>
                            <div class="notice-text">{{ currentEmotion.riskDescription }}</div>
                        </div>
                    </div>
                </div>
             </div>
             <!-- 会话列表 -->
             <div class="session-history">
                <h4 class="section-title">会话列表</h4>
                <div class="session-list">
                    <div v-for="session in sessionList" :key="session.id" @click="handleSessionClick(session)" class="session-item">
                        <div class="session-info">
                            <div class="session-title">
                                <span>{{ session.sessionTitle }}</span>
                                <div class="session-meta">
                                    <span class="session-time">{{ session.startedAt }}</span>
                                </div>
                                <div class="session-preview">
                                    {{ session.lastMessageContent }}
                                </div>
                                <div class="session-stats">
                                    <span>
                                        <el-icon><ChatRound /></el-icon>
                                        {{ session.messageCount || 0 }}
                                    </span>
                                    <span>
                                        <el-icon><Clock /></el-icon>
                                        {{ session.durationMinutes || 0 }} 分钟
                                    </span>
                                </div>
                            </div>
                            <div class="session-actions">
                                <el-button text type="danger" size="small" @click="handleDeleteSession(session.id)">
                                    <el-icon><DeleteFilled /></el-icon>
                                </el-button>
                            </div>
                        </div>
                    </div>
                </div>
             </div>
        </div>
        <div class="chat-main">
            <div class="chat-header">
                <div class="header-left">
                    <div class="chat-avatar">
                        <el-image :src="iconUrl1" style="width: 30px;height: 30px" />
                    </div>
                    <div class="chat-info">
                        <h2>宁渡AI助手</h2>
                        <p>您的贴心AI心理健康助手</p>
                    </div>
                </div>
                <el-button circle @click="createNewFrontendSession" title="新建会话">
                    <el-icon><Plus /></el-icon>
                </el-button>
            </div>
            <!-- 聊天消息区域 -->
            <div class="chat-messages">
                <div class="message-item ai-message" v-if="messages.length === 0">
                    <div class="message-avatar">
                        <el-image :src="iconUrl" style="width: 18px;height: 18px" />
                    </div>
                    <div class="message-content">
                        <div class="message-bubble">
                            <p>{{greetingText||`您好！我是小暖，您的AI心理健康助手。很高兴陪伴您，为您提供温暖的心理支持。请告诉我，今天您感觉怎么样？有什么想要分享的吗？`}}</p>
                        </div>
                        <div class="message-time">刚刚</div>
                    </div>
                </div>
                <div v-for="msg in messages" :key="msg.id" class="message-item" :class="msg.senderType === 1 ?  'user-message' : 'ai-message'">
                    <div class="message-avatar">
                        <el-image v-if="msg.senderType === 1" style="width: 18px; height:18px" :src="iconUrl2"></el-image>
                        <el-image v-if="msg.senderType === 2" style="width: 18px; height:18px" :src="iconUrl"></el-image>
                    </div>
                    <div class="message-content">
                        <div class="message-bubble">
                            <div v-if="msg.senderType === 2 && isAiTyping && !msg.content" class="typing-indicator">
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                            </div>
                            <div v-else-if="msg.isError" class="error-message">
                                <p>{{ msg.content }}</p>
                            </div>
                             <MarkdownRenderer v-else-if="msg.senderType === 2 && !msg.isError" :content="msg.content" :is-ai-message="true" />
                             <p v-else-if="msg.content" v-html="formatMessageContent(msg.content)"></p>
                        </div>
                        <div class="message-time">{{ msg.senderType === 2 && isAiTyping ? '正在输入中...' : msg.createdAt }}</div>
                    </div>
                </div>
            </div>
            <!-- 消息输入区域 -->
            <div class="chat-input">
                <div v-if="isAiTyping" class="typing-cancel-bar">
                    <span class="typing-text">AI 正在思考中...</span>
                    <el-button size="small" text type="warning" @click="forceUnlockInput">
                        取消等待
                    </el-button>
                </div>
                <div class="input-wrapper">
                    <div class="input-container">
                        <!-- 🔥 终极修复：移除 :disabled，改用 CSS 视觉禁用 + 发送拦截 -->
                        <!-- 这样即使 isAiTyping 卡死，输入框本身始终可编辑 -->
                        <el-input
                            ref="inputRef"
                            v-model="userMessage"
                            placeholder="请输入您想要分享的内容..."
                            type="textarea"
                            :rows="3"
                            :class="{ 'input-disabled-visual': isAiTyping }"
                            @keydown="handleKeyDown"
                            @compositionstart="isComposing = true"
                            @compositionend="handleCompositionEnd"
                            class="message-input"
                            clearable />
                        <div class="input-footer">
                            <span>按Enter发送，Shift+Enter换行</span>
                            <span>{{ userMessage.length }}/500</span>
                        </div>
                    </div>
                    <el-button
                        :disabled="!userMessage.trim() || userMessage.length > 500"
                        type="primary"
                        class="send-btn"
                        @click="sendMessage">
                        <el-icon><Promotion /></el-icon>
                    </el-button>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { startSession, getSessionList, deleteSession, getSessionDetail, getSessionEmotion } from '@/api/frontend'
import { ElMessage } from 'element-plus'
import { ChatRound, DeleteFilled } from '@element-plus/icons-vue'
import MarkdownRenderer from '@/components/MarkdownRenderer.vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'

const iconUrl = new URL('@/assets/images/robot-fill.png', import.meta.url).href
const iconUrl1 = new URL('@/assets/images/like.png', import.meta.url).href
const iconUrl2 = new URL('@/assets/images/users.png', import.meta.url).href

const inputRef = ref(null)
const currentSession = ref(null)
const sessionList = ref([])
const greetingText = ref('')
const messages = ref([])
const userMessage = ref('')
const isAiTyping = ref(false)

// 情绪花园
const currentEmotion = ref({
    primaryEmotion: '中性',
    emotionScore: 50,
    isNegative: false,
    riskLevel: 0,
    suggestion: '情绪状态平稳',
    improvementSuggestions: []
})

const loadSessionEmotion = (sessionId) => {
    const id = sessionId.toString().startsWith('session_') ? sessionId : `session_${sessionId}`
    getSessionEmotion(id).then(res => {
        // 后端返回可能缺失字段，合并默认值防止模板报错
        currentEmotion.value = {
            primaryEmotion: res.primaryEmotion || '中性',
            emotionScore: res.emotionScore || 50,
            isNegative: !!res.isNegative,
            riskLevel: res.riskLevel || 0,
            suggestion: res.suggestion || '情绪状态平稳',
            riskDescription: res.riskDescription || '',
            improvementSuggestions: res.improvementSuggestions || []
        }
    }).catch(err => {
        console.warn('⚠️ loadSessionEmotion 失败:', err)
    })
}

const getIntensityClass = (score) => {
    if (score >= 61) return 3
    if (score >= 31) return 2
    return 1
}

const getRiskText = (level) => {
    switch (level) {
        case 0: return '正常'
        case 1: return '关注'
        case 2: return '预警'
        case 3: return '危机'
        default: return '正常'
    }
}

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
    // isComposing 卡死保护：如果距上次 compositionend 超过 2 秒还为 true，强制重置
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


// 关键修复：watch v-model 变化，彻底解决 isComposing 卡死
watch(userMessage, (newVal, oldVal) => {
    // 当 userMessage 被 sendMessage 清空时，浏览器 compositionend 可能不触发
    // 此时 isComposing 会永久卡在 true，导致后续中文输入/按键异常
    if (oldVal !== newVal && isComposing.value) {
        isComposing.value = false
    }
})

// 强制解锁不再依赖任何状态复位，直接聚焦输入框
const forceUnlockInput = () => {
    isAiTyping.value = false
    isComposing.value = false
    nextTick(() => {
        inputRef.value?.focus()
        ElMessage.info('已解除锁定，请继续输入')
    })
}

const sendMessage = () => {
    if (!userMessage.value.trim()) return
    // 仅在发送时拦截，输入框本身不受影响
    if (isAiTyping.value) {
        ElMessage.warning('AI 正在回复中，请稍候再发送')
        return
    }

    const message = userMessage.value.trim()
    // 发送时同时重置 composition 状态，防止卡死
    isComposing.value = false
    userMessage.value = ''

    if (currentSession.value.status === 'TEMP') {
       startNewSession(message)
    } else {
        messages.value.push({
            id: Date.now(),
            senderType: 1,
            content: message,
            createdAt: new Date().toISOString()
        })
        startAIResponse(currentSession.value.sessionId, message)
    }
}

const createNewFrontendSession = () => {
    currentSession.value = {
        sessionId: `temp_${Date.now()}`,
        status: 'TEMP',
        sessionTitle: '新对话'
    }
    messages.value = []
    isAiTyping.value = false
}

const startNewSession = (message) => {
    const sessionParams = { initialMessage: message }
    if (currentSession.value.sessionTitle === '新对话') {
        sessionParams.sessionTitle = `宁渡AI助手 - ${new Date().toLocaleString()}`
    } else {
        sessionParams.sessionTitle = currentSession.value.sessionTitle
    }

    isAiTyping.value = true
    startSession(sessionParams).then(res => {
        if (res.emotionTrend) {
            updateEmotionGarden(res.emotionTrend, res.emotionSummary, res.recentDiaries)
        }

        const sessionData = {
            sessionId: res.sessionId,
            status: res.status,
            sessionTitle: sessionParams.sessionTitle
        }
        if (currentSession.value && currentSession.value.status === 'TEMP') {
            Object.assign(currentSession.value, sessionData)
        } else {
            currentSession.value = sessionData
        }
        getSessionPage()

        messages.value.push({
            id: `user_${Date.now()}`,
            senderType: 1,
            content: message,
            createdAt: new Date().toISOString()
        })

        messages.value.push({
            id: `ai_${Date.now()}`,
            senderType: 2,
            content: res.greetingText || '好的，我在听~',
            createdAt: new Date().toISOString()
        })

        loadSessionEmotion(res.sessionId)
    }).catch(err => {
        console.error('❌ startSession 失败:', err)
        ElMessage.error('创建会话失败，请稍后重试')
    }).finally(() => {
        isAiTyping.value = false
        // 关键修复：AI 回复结束后重新聚焦输入框
        nextTick(() => inputRef.value?.focus())
    })
}

const updateEmotionGarden = (emotionTrend, emotionSummary, recentDiaries) => {
    const score = emotionTrend.avgMoodScore || 5
    const scorePercent = Math.round(score * 10)

    let primaryEmotion = '中性'
    let isNegative = false
    let riskLevel = 0
    let riskDescription = ''

    if (score >= 7) {
        primaryEmotion = '愉悦'; isNegative = false; riskLevel = 0
    } else if (score >= 5) {
        primaryEmotion = '平静'; isNegative = false; riskLevel = 0
    } else if (score >= 3) {
        primaryEmotion = '低落'; isNegative = true; riskLevel = 2
        riskDescription = '情绪偏低落，建议适当运动或与朋友聊聊天'
    } else {
        primaryEmotion = '痛苦'; isNegative = true; riskLevel = 3
        riskDescription = '情绪状态较差，建议联系亲友或专业心理咨询师'
    }

    if (emotionTrend.trendDirection === 'DOWN' && score < 6) {
        riskLevel = Math.min(riskLevel + 1, 3)
    }

    currentEmotion.value = {
        primaryEmotion, emotionScore: scorePercent, isNegative,
        riskLevel, riskDescription,
        suggestion: emotionSummary || '情绪状态平稳',
        improvementSuggestions: [],
        trendDirection: emotionTrend.trendDirection
    }
}

const startAIResponse = (sessionId, userMsg) => {
    if (isAiTyping.value) {
        ElMessage.warning('AI 正在回复中')
        return
    }

    isAiTyping.value = true

    // 缩短超时到 15 秒，SSE 首字节通常 < 5s
    const safetyTimeout = setTimeout(() => {
        if (isAiTyping.value) {
            console.warn('⚠️ SSE 超时(15s)，强制解锁')
            isAiTyping.value = false
        }
    }, 15000)

    const unlock = () => {
        isAiTyping.value = false
        clearTimeout(safetyTimeout)
        // 关键修复：每次解锁后都重新聚焦输入框
        nextTick(() => inputRef.value?.focus())
    }

    const aiMsgId = `ai_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    messages.value.push({
        id: aiMsgId,
        senderType: 2,
        content: '',
        createdAt: new Date().toISOString()
    })

    let doneReceived = false

    fetchEventSource('/api/psychological-chat/stream', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Token': localStorage.getItem('token') || '',
            'Accept': 'text/event-stream'
        },
        body: JSON.stringify({ sessionId, userMessage: userMsg }),

        onopen: async (response) => {
            // 关键：如果服务器返回非 SSE 格式，立即解锁
            const ct = response.headers.get('Content-Type') || ''
            if (!ct.includes('text/event-stream')) {
                console.error('❌ 非SSE响应:', ct)
                unlock()
                // 尝试读取错误信息
                try {
                    const text = await response.text()
                    const aiMessage = messages.value.find(m => m.id === aiMsgId)
                    if (aiMessage) aiMessage.content = text || '服务异常，请重试'
                } catch {}
                return
            }
        },

        onmessage: (event) => {
            const aiMessage = messages.value.find(m => m.id === aiMsgId)
            if (!aiMessage) return

            const eventName = event.event || ''
            const raw = event.data || ''

            if (eventName === 'done') {
                doneReceived = true
                unlock()
                loadSessionEmotion(sessionId)
                return
            }

            if (eventName === 'error') {
                aiMessage.content = 'AI 回复出错了，请重试'
                unlock()
                return
            }

            try {
                const payload = JSON.parse(raw)
                if (payload.code == 200 && payload.data?.content) {
                    aiMessage.content += payload.data.content
                }
            } catch (e) {
                console.warn('⚠️ chunk 解析失败:', e)
            }
        },

        onerror: (err) => {
            console.error('❌ SSE onerror:', err)
            const aiMessage = messages.value.find(m => m.id === aiMsgId)
            if (aiMessage && !aiMessage.content) {
                aiMessage.content = 'AI 回复失败，请重试'
            }
            unlock()
            if (!doneReceived) loadSessionEmotion(sessionId)
        },

        onclose: () => {
            if (!doneReceived) {
                unlock()
                loadSessionEmotion(sessionId)
            }
        }
    })
}

const handleError = (error) => {
    const aiMessage = messages.value[messages.value.length - 1]
    if (aiMessage) aiMessage.content = 'AI回复失败，请重试'
    isAiTyping.value = false
    ElMessage.error('AI回复失败，请重试')
}

const getSessionPage = () => {
    getSessionList({ pageNum: 1, pageSize: 10 }).then(res => {
        sessionList.value = res.records
    })
}

const handleSessionClick = (session) => {
    getSessionDetail(session.id).then(res => {
        messages.value = res.data || []
    })
    loadSessionEmotion(session.id)
    currentSession.value = {
        sessionId: "session_" + session.id,
        status: 'ACTIVE',
        sessionTitle: session.sessionTitle
    }
}

const handleDeleteSession = (sessionId) => {
    deleteSession(sessionId).then(() => {
        ElMessage.success('删除成功')
        getSessionPage()
    })
}

const formatMessageContent = (content) => {
    return content.replace(/\n/g, '<br>')
}

onMounted(() => {
    getSessionPage()
    createNewFrontendSession()
})
</script>
<style scoped lang="scss">
.consultation-container {
    margin: 0 auto;
    width: 1200px;
    display: flex;
    gap: 20px;
    padding: 20px;
    .sidebar {
        width: 320px;
        .ai-assistant-info {
            margin-bottom: 20px;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 252, 248, 0.95) 100%);
            border-radius: 16px;
            padding: 16px;
            box-shadow: 0 8px 32px rgba(251, 146, 60, 0.06), 0 2px 8px rgba(0, 0, 0, 0.04);
            border: 1px solid rgba(251, 146, 60, 0.08);
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;
            .breathing-circle {
                width: 60px;
                height: 60px;
                background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 12px;
                animation: breathing 4s ease-in-out infinite;
                box-shadow: 0 6px 24px rgba(251, 146, 60, 0.25);
                position: relative;
            }
            .assistant-name {
                font-size: 16px;
                font-weight: 700;
                background: linear-gradient(135deg, #fb923c, #f59e0b);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
                text-align: center;
                background-clip: text;
                margin: 0 0 12px;
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
                    margin-right: 8px;
                    animation: pulse 2s infinite;
                    box-shadow: 0 0 8px rgba(5, 150, 105, 0.4);
                }
            }
        }
        .session-history {
            background: white;
            border-radius: 16px;
            padding: 16px;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
            min-height: 250px;
            display: flex;
            flex-direction: column;
            .section-title {
                font-size: 16px;
                font-weight: 600;
                color: #333;
                margin: 0 0 16px;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .session-list {
                overflow-y: auto;
                max-height: 200px;
                scrollbar-width: thin;
                scrollbar-color: rgba(64, 150, 255, 0.3) transparent;
                .session-item {
                    position: relative;
                    display: flex;
                    align-items: flex-start;
                    gap: 12px;
                    padding: 12px;
                    margin-bottom: 8px;
                    border-radius: 12px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    border: 2px solid transparent;
                    &:hover {
                        background: #f8f9ff;
                        border-color: #e6f0ff;
                    }
                    &.active {
                        background: #e6f0ff;
                        border-color: #4096ff;
                    }
                    .session-info {
                        flex: 1;
                        .session-title {
                            font-weight: 500;
                            font-size: 14px;
                            color: #333;
                            margin-bottom: 4px;
                            white-space: nowrap;
                            overflow: hidden;
                            text-overflow: ellipsis;
                            .session-meta {
                                display: flex;
                                align-items: center;
                                gap: 8px;
                                margin-bottom: 6px;
                                .session-time { font-size: 12px; color: #999; }
                            }
                            .session-preview {
                                width: 200px;
                                font-size: 12px;
                                color: #666;
                                margin-bottom: 6px;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                            }
                            .session-stats {
                                display: flex;
                                align-items: center;
                                gap: 12px;
                                span {
                                    font-size: 12px;
                                    color: #999;
                                    display: flex;
                                    align-items: center;
                                    gap: 4px;
                                }
                            }
                        }
                        .session-actions {
                            position: absolute;
                            top: 10px;
                            right: 12px;
                        }
                    }
                }
            }
        }
        .emotion-garden {
            background: linear-gradient(135deg, #fef9e7 0%, #fcf4e6 50%, #f6f0e8 100%);
            border-radius: 20px;
            padding: 16px;
            margin-bottom: 20px;
            box-shadow: 0 8px 32px rgba(252, 244, 230, 0.8);
            border: 1px solid rgba(255, 255, 255, 0.2);
            position: relative;
            overflow: hidden;
            min-height: 300px;
            .garden-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 20px;
                position: relative;
                z-index: 2;
                .garden-title {
                    display: flex;
                    align-items: center;
                    gap: 8px;
                    font-size: 16px;
                    font-weight: 600;
                    color: #8b4513;
                }
            }
            .emotion-info {
                margin: 0 auto;
                width: 80px;
                height: 80px;
                border-radius: 50%;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                z-index: 10;
                box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
                border: 2px solid rgba(255, 255, 255, 0.8);
                background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
                color: #fff;
                .emotion-name { font-size: 15px; font-weight: 600; line-height: 1; margin-bottom: 2px; }
                .emotion-score { font-size: 14px; font-weight: 700; opacity: 0.9; }
            }
            .warm-tips {
                text-align: center;
                margin-bottom: 16px;
                .emotion-status-text {
                    margin-bottom: 12px;
                    .status-label { font-size: 14px; color: #8b7355; margin-right: 8px; }
                    .status-emotion { font-size: 16px; font-weight: 600; padding: 4px 12px; border-radius: 16px; display: inline-block; }
                }
                .emotion-intensity {
                    margin-bottom: 16px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    gap: 8px;
                    .intensity-dots {
                        display: flex;
                        gap: 4px;
                        .dot {
                            width: 8px; height: 8px; border-radius: 50%; background: #e0e0e0; transition: all 0.3s ease;
                            &.active { background: linear-gradient(135deg, #ff9a9e, #fecfef); transform: scale(1.2); box-shadow: 0 2px 8px rgba(255, 154, 158, 0.4); }
                        }
                    }
                    .intensity-text { font-size: 12px; color: #8b7355; font-weight: 500; }
                }
                .warm-suggestion {
                    background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.8));
                    border-radius: 16px; padding: 12px; margin-bottom: 16px;
                    display: flex; align-items: flex-start; gap: 10px;
                    border: 1px solid rgba(255, 255, 255, 0.6); box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
                    .suggestion-icon { font-size: 20px; flex-shrink: 0; margin-top: 2px; }
                    .suggestion-content {
                        text-align: left; flex: 1;
                        .suggestion-title { font-size: 14px; font-weight: 600; color: #8b7355; margin-bottom: 6px; }
                        .suggestion-text { font-size: 13px; color: #6b5b47; line-height: 1.5; }
                    }
                }
                .healing-actions {
                    margin-bottom: 16px;
                    .actions-title { display: flex; align-items: center; justify-content: center; gap: 8px; font-size: 14px; font-weight: 600; color: #8b7355; margin-bottom: 16px; }
                    .actions-list {
                        display: flex; flex-direction: column; gap: 10px;
                        .action-item {
                            background: linear-gradient(135deg, rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.7));
                            border-radius: 12px; padding: 12px; display: flex; align-items: center; gap: 10px;
                            border: 1px solid rgba(255, 255, 255, 0.5); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); text-align: left;
                            .action-icon { font-size: 14px; color: #ffd700; flex-shrink: 0; }
                            .action-text { font-size: 12px; color: #6b5b47; line-height: 1.4; flex: 1; }
                        }
                    }
                }
                .risk-notice {
                    background: linear-gradient(135deg, #fff9e6, #ffeaa7); border-radius: 16px; padding: 16px;
                    display: flex; align-items: flex-start; gap: 12px;
                    border: 1px solid rgba(255, 234, 167, 0.6); box-shadow: 0 6px 20px rgba(255, 234, 167, 0.3);
                    .notice-icon { font-size: 20px; flex-shrink: 0; margin-top: 2px; }
                    .notice-content {
                        flex: 1;
                        .notice-title { font-size: 14px; font-weight: 600; color: #d4840f; margin-bottom: 6px; }
                        .notice-text { font-size: 13px; color: #b8740c; line-height: 1.5; }
                    }
                }
            }
        }
    }
    .chat-main {
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 252, 250, 0.98) 100%);
        border-radius: 20px;
        box-shadow: 0 12px 40px rgba(251, 146, 60, 0.08), 0 4px 16px rgba(0, 0, 0, 0.04);
        border: 1px solid rgba(251, 146, 60, 0.1);
        backdrop-filter: blur(10px);
        display: flex;
        flex-direction: column;
        overflow: hidden;
        flex: 1;
        .chat-header {
            background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%);
            color: white;
            padding: 20px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: relative;
            flex-shrink: 0;
            .header-left {
                display: flex;
                align-items: center;
                .chat-avatar {
                    width: 48px; height: 48px; background: rgba(255, 255, 255, 0.25); border-radius: 50%;
                    display: flex; align-items: center; justify-content: center; margin-right: 16px;
                    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); position: relative; z-index: 1;
                }
                .chat-info {
                    h2 { font-size: 20px; font-weight: 700; margin-bottom: 4px; }
                    p { font-size: 14px; }
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
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.02) 0%, rgba(255, 252, 248, 0.05) 100%);
            min-height: 0;
            max-height: calc(100vh - 200px);
            scrollbar-width: thin;
            scrollbar-color: rgba(251, 146, 60, 0.3) transparent;
            .message-item {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                .message-avatar {
                    width: 32px; height: 32px; border-radius: 50%;
                    display: flex; align-items: center; justify-content: center;
                    font-size: 14px; color: white; flex-shrink: 0;
                }
                &.ai-message .message-avatar { background: linear-gradient(135deg, #fb923c, #f59e0b); box-shadow: 0 4px 12px rgba(251, 146, 60, 0.3); }
                &.user-message .message-avatar { background: linear-gradient(135deg, #6b7280, #4b5563); box-shadow: 0 4px 12px rgba(107, 114, 128, 0.3); }
                .message-content {
                    max-width: 70%;
                    .message-bubble {
                        background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(255, 252, 248, 0.95) 100%);
                        border-radius: 16px; padding: 12px 16px; position: relative;
                        animation: fadeInUp 0.4s ease-out;
                        border: 1px solid rgba(251, 146, 60, 0.1); box-shadow: 0 4px 16px rgba(251, 146, 60, 0.05);
                        .typing-indicator {
                            display: flex; gap: 4px; padding: 8px 0;
                            .typing-dot {
                                width: 8px; height: 8px; background: #ccc; border-radius: 50%;
                                animation: typing 1.5s ease-in-out infinite;
                                &:nth-child(2) { animation-delay: 0.2s; }
                                &:nth-child(3) { animation-delay: 0.4s; }
                            }
                        }
                        .error-message {
                            background: linear-gradient(135deg, #FEF2F2 0%, #FECACA 100%);
                            border: 1px solid #F87171; border-radius: 12px; padding: 12px 16px;
                            color: #991B1B; font-weight: 500; display: flex; align-items: center; gap: 8px;
                        }
                    }
                    .message-time { font-size: 12px; color: #999; margin-top: 4px; }
                }
            }
        }
        .chat-input {
            border-top: 1px solid rgba(251, 146, 60, 0.1);
            padding: 20px 24px;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.5) 0%, rgba(255, 252, 248, 0.7) 100%);
            backdrop-filter: blur(10px);
            flex-shrink: 0;
            .typing-cancel-bar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 8px 12px;
                margin-bottom: 12px;
                background: rgba(251, 146, 60, 0.08);
                border-radius: 8px;
                font-size: 13px;
                color: #b45309;
            }
            .input-wrapper {
                display: flex;
                gap: 12px;
                align-items: flex-end;
            }
            .input-container {
                flex: 1;
            }
            /* 🔥 视觉禁用样式：降低透明度但不阻止交互 */
            .input-disabled-visual {
                opacity: 0.6;
                transition: opacity 0.3s ease;
            }
            .input-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 12px;
                color: #78716c;
                font-weight: 500;
            }
            .send-btn {
                height: 60px;
                width: 60px;
                border-radius: 16px;
                background: linear-gradient(135deg, #fb923c 0%, #f59e0b 100%) !important;
                border: none !important;
                box-shadow: 0 6px 20px rgba(251, 146, 60, 0.25);
                transition: all 0.3s ease;
            }
        }
    }
}
</style>