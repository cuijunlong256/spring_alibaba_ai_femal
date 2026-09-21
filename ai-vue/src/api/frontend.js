import service from '@/utils/request'

export const register = (data) => {
    return service.post('/user/add', data)
}

export const startSession = (data) => {
    return service.post('/psychological-chat/session/start', data)
}

export const getSessionList = (params) => {
    return service.get('/psychological-chat/sessions', { params })
}

export const deleteSession = (sessionId) => {
    return service.delete(`/psychological-chat/sessions/${sessionId}`)
}

export const getSessionDetail = (sessionId) => {
    return service.get(`/psychological-chat/sessions/${sessionId}/messages`)
}

export const getSessionEmotion = (sessionId) => {
    return service.get(`/psychological-chat/session/${sessionId}/emotion`)
}

export const addEmotionDiary = (data) => {
    return service.post('/emotion-diary', data)
}

export const getKnowledgeList = (params) => {
    return service.get('/knowledge/article/page', { params })
}

export const getKnowledgeDetail = (articleId) => {
    return service.get(`/knowledge/article/${articleId}`)
}

// ==================== 情感红娘相关接口 ====================

// 根据性别获取接口前缀：male用户推荐女性 -> /female-matchmaker；female用户推荐男性 -> /male-matchmaker
const getMatchmakerPrefix = (gender) => {
    return gender === 'male' ? '/female-matchmaker' : '/male-matchmaker'
}

// 创建红娘会话
export const startMatchmakerSession = (gender, data) => {
    return service.post(`${getMatchmakerPrefix(gender)}/session/start`, data)
}

// 获取红娘会话列表
export const getMatchmakerSessionList = (gender, params) => {
    return service.get(`${getMatchmakerPrefix(gender)}/sessions`, { params })
}

// 删除红娘会话
export const deleteMatchmakerSession = (gender, sessionId) => {
    return service.delete(`${getMatchmakerPrefix(gender)}/sessions/${sessionId}`)
}

// 获取红娘会话历史消息
export const getMatchmakerSessionMessages = (gender, sessionId) => {
    return service.get(`${getMatchmakerPrefix(gender)}/sessions/${sessionId}/messages`)
}

// SSE 流式对话的完整 URL（给 fetchEventSource 用）
export const getMatchmakerStreamUrl = (gender) => {
    return `/api${getMatchmakerPrefix(gender)}/stream`
}