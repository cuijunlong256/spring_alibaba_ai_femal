<template>
    <div class="matchmaker-container">
        <!-- 顶部 Hero 区 -->
        <div class="hero-section">
            <div class="hero-content">
                <div class="hero-icon">💝</div>
                <h1 class="hero-title">情感红娘</h1>
                <p class="hero-subtitle">AI 智能红娘，为你推荐最合适的心动对象</p>
                <p class="hero-desc">根据你的性别，从专属知识库中精选优质对象，让 AI 红娘为你牵线搭桥</p>
            </div>
        </div>

        <!-- 性别选择区 -->
        <div class="gender-section">
            <div class="section-header">
                <h2>选择你的身份</h2>
                <p>不同性别将匹配不同的对象知识库</p>
            </div>
            <div class="gender-cards">
                <div
                    class="gender-card"
                    :class="{ active: selectedGender === 'female' }"
                    @click="selectGender('female')"
                >
                    <div class="gender-icon">👩</div>
                    <div class="gender-name">我是女生</div>
                    <div class="gender-desc">为你推荐 30+ 优质男明星</div>
                </div>
                <div
                    class="gender-card"
                    :class="{ active: selectedGender === 'male' }"
                    @click="selectGender('male')"
                >
                    <div class="gender-icon">👨</div>
                    <div class="gender-name">我是男生</div>
                    <div class="gender-desc">为你推荐 50+ 优质女明星</div>
                </div>
            </div>
                        <div class="start-chat-wrap" v-if="selectedGender">
                            <el-button type="danger" size="large" round @click="startChatting">
                                <el-icon><Promotion /></el-icon>
                                开始红娘聊天
                            </el-button>
                        </div>
        </div>

        <!-- 推荐对象列表区 -->
        <div class="recommend-section" v-if="selectedGender">
            <div class="section-header">
                <h2>为你推荐</h2>
                <p>来自{{ selectedGender === 'female' ? '男性' : '女性' }}知识库的精选对象</p>
            </div>

            <!-- 搜索和筛选 -->
            <div class="filter-bar">
                <el-input
                    v-model="searchKeyword"
                    placeholder="搜索对象姓名..."
                    clearable
                    class="search-input"
                >
                    <template #prefix>
                        <el-icon><Search /></el-icon>
                    </template>
                </el-input>
                <el-select v-model="filterTag" placeholder="筛选标签" clearable class="filter-select">
                    <el-option
                        v-for="tag in availableTags"
                        :key="tag"
                        :label="tag"
                        :value="tag"
                    />
                </el-select>
            </div>

            <!-- 对象卡片网格 -->
            <div class="partner-grid">
                <div
                    v-for="partner in filteredPartners"
                    :key="partner.id"
                    class="partner-card"
                    :class="{ selected: selectedPartner?.id === partner.id }"
                    @click="selectPartner(partner)"
                >
                    <div class="partner-avatar">
                        <el-image :src="partner.avatar" fit="cover" class="avatar-img" />
                    </div>
                    <div class="partner-info">
                        <div class="partner-name-row">
                            <span class="partner-name">{{ partner.name }}</span>
                            <span class="partner-age">{{ partner.age }}岁</span>
                        </div>
                        <div class="partner-occupation">{{ partner.occupation }}</div>
                        <div class="partner-tags">
                            <el-tag
                                v-for="tag in partner.tags"
                                :key="tag"
                                size="small"
                                type="danger"
                                effect="light"
                                class="partner-tag"
                            >
                                {{ tag }}
                            </el-tag>
                        </div>
                        <div class="partner-bio">{{ partner.bio }}</div>
                    </div>
                </div>
            </div>

            <div class="empty-tip" v-if="filteredPartners.length === 0">
                暂无匹配的对象
            </div>
        </div>

        <!-- 对象详情 & AI 红娘介绍区 -->
        <div class="detail-section" v-if="selectedPartner">
            <div class="detail-card">
                <div class="detail-header">
                    <el-image :src="selectedPartner.avatar" fit="cover" class="detail-avatar" />
                    <div class="detail-basic">
                        <h2>{{ selectedPartner.name }}</h2>
                        <p>{{ selectedPartner.age }}岁 · {{ selectedPartner.occupation }}</p>
                        <div class="detail-tags">
                            <el-tag
                                v-for="tag in selectedPartner.tags"
                                :key="tag"
                                size="small"
                                type="danger"
                                effect="light"
                            >
                                {{ tag }}
                            </el-tag>
                        </div>
                    </div>
                </div>

                <div class="detail-body">
                    <div class="detail-bio">
                        <h3>个人简介</h3>
                        <p>{{ selectedPartner.detail }}</p>
                    </div>

                    <!-- AI 红娘介绍 -->
                    <div class="ai-intro">
                        <div class="ai-intro-header">
                            <span class="ai-badge">🤖 AI 红娘</span>
                            <span class="ai-intro-title">心动推荐理由</span>
                        </div>
                        <div class="ai-intro-content">
                            <p>{{ aiIntroText }}</p>
                        </div>
                        <el-button type="danger" @click="regenerateIntro">
                            <el-icon><Refresh /></el-icon>
                            重新生成介绍
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Refresh, Promotion } from '@element-plus/icons-vue'

const router = useRouter()

// 性别选择
const selectedGender = ref('')

// 搜索筛选
const searchKeyword = ref('')
const filterTag = ref('')

// 选中的对象
const selectedPartner = ref(null)
const aiIntroText = ref('')



// 女性用户 -> 男明星数据（模拟知识库）
const malePartners = ref([
    {
        id: 1,
        name: '彭于晏',
        age: 42,
        occupation: '演员',
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        tags: ['阳光帅气', '健身达人', '演技派'],
        bio: '阳光帅气的实力派演员，热爱健身与运动。',
        detail: '彭于晏，中国台湾男演员、歌手。凭借阳光健康的形象和出色的演技深受观众喜爱。代表作《翻滚吧！阿信》《湄公河行动》等。生活中热爱运动，自律性极强。'
    },
    {
        id: 2,
        name: '胡歌',
        age: 42,
        occupation: '演员',
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        tags: ['儒雅绅士', '实力派', '低调'],
        bio: '儒雅低调的实力派男演员，内外兼修。',
        detail: '胡歌，中国内地男演员、歌手。因饰演《仙剑奇侠传》李逍遥一角走红，后凭借《琅琊榜》梅长苏再创事业高峰。为人低调谦逊，热爱摄影与阅读。'
    },
    {
        id: 3,
        name: '易烊千玺',
        age: 24,
        occupation: '歌手/演员',
        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        tags: ['年轻有为', '多才多艺', '沉稳'],
        bio: '年轻有为的全能艺人，沉稳内敛。',
        detail: '易烊千玺，中国内地男演员、歌手、舞者，TFBOYS成员之一。年少成名却沉稳内敛，在影视、音乐、舞蹈领域均有出色表现。'
    }
])

// 男性用户 -> 女明星数据（模拟知识库）
const femalePartners = ref([
    {
        id: 101,
        name: '迪丽热巴',
        age: 32,
        occupation: '演员',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        tags: ['美艳大方', '演技在线', '异域风情'],
        bio: '美艳大方的新疆女演员，人气极高。',
        detail: '迪丽热巴，中国内地影视女演员。新疆乌鲁木齐人，因主演《克拉恋人》《三生三世十里桃花》等剧走红，五官精致立体，性格开朗活泼。'
    },
    {
        id: 102,
        name: '赵丽颖',
        age: 37,
        occupation: '演员',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        tags: ['甜美可爱', '实力派', '励志'],
        bio: '甜美可爱的励志女神，演技扎实。',
        detail: '赵丽颖，中国内地女演员。从龙套一路成长为一线女星，代表作《花千骨》《知否知否应是绿肥红瘦》等。圆脸甜美，性格坚韧独立。'
    },
    {
        id: 103,
        name: '杨幂',
        age: 38,
        occupation: '演员/制片人',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        tags: ['时尚女王', '独立自强', '高情商'],
        bio: '时尚独立的女强人，情商与智商双高。',
        detail: '杨幂，中国内地女演员、歌手、制片人。代表作《宫锁心玉》《三生三世十里桃花》等。时尚品味出众，独立自强，是圈内公认的高情商代表。'
    }
])

// 当前展示的对象列表
const currentPartners = computed(() => {
    return selectedGender.value === 'female' ? malePartners.value : femalePartners.value
})

// 可用标签
const availableTags = computed(() => {
    const tags = new Set()
    currentPartners.value.forEach(p => p.tags.forEach(t => tags.add(t)))
    return Array.from(tags)
})

// 过滤后的对象
const filteredPartners = computed(() => {
    let list = currentPartners.value
    if (searchKeyword.value) {
        list = list.filter(p => p.name.includes(searchKeyword.value))
    }
    if (filterTag.value) {
        list = list.filter(p => p.tags.includes(filterTag.value))
    }
    return list
})

// 选择性别
const selectGender = (gender) => {
    selectedGender.value = gender
    selectedPartner.value = null
    aiIntroText.value = ''
}

// 开始红娘聊天
const startChatting = () => {
    if (!selectedGender.value) return
    router.push({ path: '/matchmaker/chat', query: { gender: selectedGender.value } })
}

// 选择对象
const selectPartner = (partner) => {
    selectedPartner.value = partner
    aiIntroText.value = `根据你的选择，我为你隆重介绍 ${partner.name}！${partner.name} 今年 ${partner.age} 岁，是一位${partner.occupation}。${partner.bio} 相信你们会很合拍~`
}

// 重新生成 AI 介绍
const regenerateIntro = () => {
    if (!selectedPartner.value) return
    aiIntroText.value = `让我再为你详细说说 ${selectedPartner.value.name}~ 作为${selectedPartner.value.occupation}，TA 不仅外形出众，内在也十分优秀。标签"${selectedPartner.value.tags.join('、')}"很好地概括了 TA 的特质。你觉得怎么样呢？`
}


</script>

<style scoped lang="scss">
.matchmaker-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

/* Hero 区 */
.hero-section {
    background: linear-gradient(135deg, #ff6b9d 0%, #ffa8c5 50%, #ffc3d9 100%);
    border-radius: 20px;
    padding: 50px 30px;
    text-align: center;
    color: white;
    margin-bottom: 30px;
    box-shadow: 0 10px 40px rgba(255, 107, 157, 0.3);
    .hero-icon {
        font-size: 60px;
        margin-bottom: 16px;
        animation: heartbeat 1.5s ease-in-out infinite;
    }
    .hero-title {
        font-size: 38px;
        font-weight: 700;
        margin: 0 0 12px;
    }
    .hero-subtitle {
        font-size: 20px;
        margin: 0 0 8px;
        opacity: 0.95;
    }
    .hero-desc {
        font-size: 14px;
        opacity: 0.85;
        margin: 0;
    }
}

@keyframes heartbeat {
    0%, 100% { transform: scale(1); }
    50% { transform: scale(1.15); }
}

/* 区块通用 */
.section-header {
    text-align: center;
    margin-bottom: 24px;
    h2 {
        font-size: 24px;
        color: #333;
        margin: 0 0 8px;
    }
    p {
        font-size: 14px;
        color: #888;
        margin: 0;
    }
}

/* 性别选择 */
.gender-section {
    margin-bottom: 40px;
}
.gender-cards {
    display: flex;
    gap: 24px;
    justify-content: center;
}
.gender-card {
    flex: 1;
    max-width: 360px;
    padding: 36px 24px;
    background: white;
    border-radius: 16px;
    text-align: center;
    cursor: pointer;
    border: 2px solid transparent;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(255, 107, 157, 0.15);
    }
    &.active {
        border-color: #ff6b9d;
        background: linear-gradient(135deg, #fff0f5 0%, #ffe4ec 100%);
    }
    .gender-icon {
        font-size: 56px;
        margin-bottom: 16px;
    }
    .gender-name {
        font-size: 20px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
    }
    .gender-desc {
        font-size: 13px;
        color: #999;
    }
}

/* 筛选栏 */
.filter-bar {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
    .search-input {
        flex: 1;
    }
    .filter-select {
        width: 200px;
    }
}

/* 对象卡片网格 */
.recommend-section {
    margin-bottom: 40px;
}
.partner-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}
.partner-card {
    background: white;
    border-radius: 16px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(255, 107, 157, 0.2);
    }
    &.selected {
        border-color: #ff6b9d;
    }
    .partner-avatar {
        width: 100%;
        height: 220px;
        background: linear-gradient(135deg, #ffd1dc, #ffe4ec);
        display: flex;
        align-items: center;
        justify-content: center;
        .avatar-img {
            width: 100%;
            height: 100%;
        }
    }
    .partner-info {
        padding: 16px;
        .partner-name-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 6px;
            .partner-name {
                font-size: 18px;
                font-weight: 600;
                color: #333;
            }
            .partner-age {
                font-size: 13px;
                color: #ff6b9d;
                font-weight: 500;
            }
        }
        .partner-occupation {
            font-size: 13px;
            color: #888;
            margin-bottom: 10px;
        }
        .partner-tags {
            margin-bottom: 10px;
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
            .partner-tag {
                border-radius: 10px;
            }
        }
        .partner-bio {
            font-size: 13px;
            color: #666;
            line-height: 1.5;
        }
    }
}
.empty-tip {
    text-align: center;
    padding: 40px;
    color: #999;
}

/* 详情区 */
.detail-section {
    margin-bottom: 40px;
}
.detail-card {
    background: white;
    border-radius: 20px;
    padding: 28px;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}
.detail-header {
    display: flex;
    gap: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 24px;
    .detail-avatar {
        width: 120px;
        height: 120px;
        border-radius: 16px;
        flex-shrink: 0;
    }
    .detail-basic {
        h2 {
            font-size: 26px;
            margin: 0 0 8px;
            color: #333;
        }
        p {
            font-size: 14px;
            color: #888;
            margin: 0 0 12px;
        }
        .detail-tags {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
    }
}
.detail-body {
    .detail-bio {
        margin-bottom: 24px;
        h3 {
            font-size: 16px;
            color: #333;
            margin: 0 0 12px;
        }
        p {
            font-size: 14px;
            color: #555;
            line-height: 1.7;
            margin: 0;
        }
    }
    .ai-intro {
        background: linear-gradient(135deg, #fff0f5 0%, #ffe4ec 100%);
        border-radius: 16px;
        padding: 20px;
        .ai-intro-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 12px;
            .ai-badge {
                background: #ff6b9d;
                color: white;
                padding: 4px 12px;
                border-radius: 12px;
                font-size: 12px;
                font-weight: 500;
            }
            .ai-intro-title {
                font-size: 15px;
                font-weight: 600;
                color: #333;
            }
        }
        .ai-intro-content {
            font-size: 14px;
            color: #555;
            line-height: 1.7;
            margin-bottom: 16px;
        }
    }
}



.start-chat-wrap {
    text-align: center;
    margin-top: 24px;
    .el-button {
        padding: 16px 48px;
        font-size: 16px;
        box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
    }
}
</style>