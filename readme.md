# AIGC-KPS-Agent 智能陪伴平台

基于 Spring AI + Spring AI Alibaba 的 AI 智能陪伴后端服务，包含 **AI 心理咨询** 和 **情感红娘** 两大核心功能模块。采用 StateGraph 工作流编排多 Agent 协作，结合 pgvector 向量数据库实现明星知识库的语义检索。

## 一、项目架构

### 1.1 技术栈

| 分类 | 技术选型 | 版本 |
| --- | --- | --- |
| 基础框架 | Spring Boot | 3.5.8 |
| AI 框架 | Spring AI | 1.1.2 |
| AI 工作流 | Spring AI Alibaba (StateGraph / ReactAgent) | 1.1.2.2 |
| 本地 LLM | Ollama (qwen2.5:7b) | - |
| 远程 LLM | 腾讯云 GLM-5.1 / GLM-5-Turbo | - |
| 关系型数据库 | MySQL | 8.x |
| 向量数据库 | PostgreSQL + pgvector | 15+ |
| ORM | MyBatis-Plus | 3.5.7 |
| 认证 | JWT (java-jwt) | 4.4.0 |
| 构建工具 | Maven | - |

### 1.2 系统架构图

```text
┌─────────────────────────────────────────────────────────────┐
│                      前端 (Vue3 + Element Plus)             │
│           心理咨询页面 | 性别选择页 | 红娘聊天页 (SSE 流式)     │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP / SSE
┌──────────────────────────▼──────────────────────────────────┐
│                 Spring Boot 后端 (端口 1236)                │
│                                                             │
│  ┌──────────────┐   ┌────────────────┐   ┌─────────────────┐│
│  │ UserController│   │PsychologicalChat│   │Female/MaleGraph ││
│  │ (登录注册)    │   │ (心理咨询)      │   │ ChatController  ││
│  └──────┬───────┘   └───────┬────────┘   └────────┬────────┘│
│         │                   │                     │         │
│  ┌──────▼──────────────────▼─────────────────────▼────────┐ │
│  │                     Service 层                         │ │
│  │  UserService | PsychologicalSupportService             │ │
│  │  MatchmakerService | MatchmakerSessionService          │ │
│  │  MatchmakerMessageService | MatchmakerRecommendation   │ │
│  └──────────────────────────┬─────────────────────────────┘ │
│                             │                               │
│  ┌──────────────────────────▼─────────────────────────────┐ │
│  │             StateGraph 工作流 (ReactAgent)               │ │
│  │                                                        │ │
│  │  心理咨询流: START → 风险评估 → (人工审核 / 心理引导)     │ │
│  │  红娘工作流: START → 风险评估 → (人工审核 / 红娘推荐)     │ │
│  └──────────────────────────┬─────────────────────────────┘ │
│                             │                               │
│  ┌──────────────────────────▼─────────────────────────────┐ │
│  │                     Tool 工具层                        │ │
│  │                PartnerSearchTool (明星检索)              │ │
│  │  ┌───────────────┴────────────────┐                    │ │
│  │  │ 语义向量检索(pgvector)         │                    │ │
│  │  │ 数据库精确过滤(星座/年龄/国籍) │                    │ │
│  │  │ 上下文指代识别(她们/除了)      │                    │ │
│  │  └───┴────────────────────────────────┴───────────────┘ │ │
│  └──────────────────────────────────┬───────────────────────┘ │
└─────────────────────────────────────┼───────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────┐
          │                           │                       │
   ┌──────▼──────┐           ┌────────▼────────┐     ┌────────▼────────┐
   │    MySQL    │           │   PostgreSQL    │     │     Ollama      │
   │   业务数据   │           │   pgvector      │     │   qwen2.5:7b    │
   │  用户/会话   │           │   明星向量库     │     │    本地大模型    │
   └─────────────┘           └─────────────────┘     └─────────────────┘
```

### 1.3 目录结构

```text
com.atguigu.study
├── agent/                          # AI Agent 配置（提示词 + ReactAgent 定义）
│   ├── EmotionAnalyzeAgentConfig.java        # 情绪分析 Agent
│   ├── RiskAssessmentAgentConfig.java        # 心理风险评估 Agent
│   ├── PsychologicalGuideAgentConfig.java    # 心理引导 Agent
│   ├── FemaleMatchmakerAgentConfig.java      # 女性红娘推荐 Agent（男用户）
│   ├── FemaleMatchmakerRiskAgentConfig.java  # 女性红娘风险评估 Agent
│   ├── MaleMatchmakerAgentConfig.java        # 男性红娘推荐 Agent（女用户）
│   └── MaleMatchmakerRiskAgentConfig.java    # 男性红娘风险评估 Agent
├── config/                         # 配置类
│   ├── LllmConfig.java                       # LLM 模型配置（Ollama / 远程）
│   ├── PsychologicalGraphConfig.java         # 心理咨询工作流
│   ├── FemaleGraphConfig.java                # 女性红娘工作流
│   ├── MaleGraphConfig.java                  # 男性红娘工作流
│   ├── MysqlDataSourceConfig.java            # MySQL 数据源
│   ├── PgVectorDataSourceConfig.java         # PostgreSQL 数据源
│   └── SecurityConfig.java                   # Spring Security + JWT
├── controller/                     # 接口层
│   ├── PsychologicalChat.java                # 心理咨询 SSE 接口
│   ├── FemaleGraphChatController.java        # 女性红娘 SSE 接口
│   ├── MaleGraphChatController.java          # 男性红娘 SSE 接口
│   └── UserController.java                   # 用户登录注册
├── service/ai/                     # AI 业务服务
│   ├── PsychologicalSupportService.java      # 心理咨询核心服务
│   ├── MatchmakerService.java                # 红娘核心服务（男女共用）
│   ├── MatchmakerSessionService.java         # 红娘会话管理
│   ├── MatchmakerMessageService.java         # 红娘消息管理
│   ├── MatchmakerRecommendationService.java  # 推荐记录管理
│   └── RecommendationContext.java            # 推荐上下文（ThreadLocal）
├── tool/
│   └── PartnerSearchTool.java      # 明星检索工具（向量+数据库混合检索）
├── domain/                         # 实体类（MySQL + PostgreSQL）
├── mapper/                         # MyBatis Mapper（MySQL）
├── pgmapper/                       # MyBatis Mapper（PostgreSQL）
└── util/
    ├── PartnerDataImporter.java    # 明星数据导入（JSON → 向量库）
    └── JwtTokenUtil.java           # JWT 工具
```

## 二、核心功能

### 2.1 AI 心理咨询

工作流：

```text
START → riskAssessmentAgent（风险评估）
  │
  ├── LOW/MEDIUM → psychologicalGuideAgent（心理引导）→ END
  └── HIGH/CRITICAL → human_review（危机干预回复）→ END
```

功能特点：

- 风险评估 Agent 检测自杀、自伤、极端情绪等高危信号
- 低风险用户进入心理引导，像朋友一样自然对话
- 高风险用户触发危机干预，提供心理援助热线
- 支持 SSE 流式输出，对话实时展示

涉及表：

- `consultation_session`（type=PSYCHOLOGICAL）
- `consultation_message`

### 2.2 情感红娘

工作流（男女红娘独立但逻辑一致）：

```text
START → MatchmakerRiskAgent（风险评估）
  │
  ├── LOW/MEDIUM → MatchmakerAgent（红娘推荐）→ END
  └── HIGH/CRITICAL → human_review（违规警告）→ END
```

功能特点：

- 男女分工作流：男性用户推荐女明星（50+），女性用户推荐男明星（30+）
- 混合检索：pgvector 语义检索 + MySQL 精确过滤（星座、年龄、国籍）
- 上下文感知：
    - 识别“她们谁喜欢读书” → 从已推荐记录中筛选
    - 识别“除了她们” → 排除已推荐，推荐新人
    - 历史对话拼接为检索 query，保证上下文连贯
- 推荐去重：`matchmaker_recommendation` 表记录已推荐明星，避免重复
- SSE 流式输出：实时展示红娘回复

涉及表：

- `consultation_session`（type=FEMALE_MATCHMAKER / MALE_MATCHMAKER）
- `consultation_message`
- `matchmaker_recommendation`（推荐记录）
- `partner`（PostgreSQL，明星资料）

### 2.3 明星检索工具 PartnerSearchTool

核心检索策略：

```java
if (排除型指代: "除了她们"、"还有其他人吗") {
    // 排除已推荐，重新检索（精确条件走数据库，否则走向量）
} else if (筛选型指代: "她们谁喜欢读书") {
    // 从当前会话已推荐记录中筛选
} else if (有精确条件: 星座/年龄/国籍) {
    // 数据库全表过滤，保证精确匹配
} else {
    // pgvector 语义向量检索
}
```

## 三、开发过程中的难点与解决方案

### 3.1 多数据源配置（MySQL + PostgreSQL）

难点：项目同时使用 MySQL（业务数据）和 PostgreSQL（pgvector 向量库），MyBatis 的 Mapper 扫描和事务管理容易冲突。

解决方案：

- 分别配置 `MysqlDataSourceConfig` 和 `PgVectorDataSourceConfig`
- MySQL Mapper 放在 `mapper/` 包，PostgreSQL Mapper 放在 `pgmapper/` 包
- 各自指定 `@MapperScan` 路径，互不干扰
- `application.yaml` 中使用 `spring.datasource.jdbc-url`（而非 `url`）避免自定义数据源报错

### 3.2 MyBatis XML 加载失败

报错：`Invalid bound statement (not found): UserMapper.selectByExample`

原因：MyBatis 找不到 Mapper XML 文件。

解决：

- 确认 `target/classes/mapper/` 下 XML 文件存在
- `application.yaml` 配置 `mybatis-plus.mapper-locations: classpath*:/mapper/**/*.xml`
- 执行 `mvn clean compile` 重新编译

### 3.3 上下文感知与指代词识别

难点：用户说“她们两个谁喜欢读书”，AI 无法理解“她们”指代上一轮推荐的明星。

解决：

- `PartnerSearchTool` 中实现 `detectReferenceType()` 方法
- 区分“筛选型指代”（她们谁）和“排除型指代”（除了她们）
- 从 `matchmaker_recommendation` 表查询当前会话已推荐的明星
- 筛选型：在已推荐列表中按关键词过滤
- 排除型：从检索结果中排除已推荐 ID

### 3.4 精确属性过滤（星座/年龄）

难点：纯向量检索的 topK 结果可能不包含精确匹配（如用户要狮子座，返回的可能是水瓶座）。

解决：

- 检测到星座、年龄、国籍等精确条件时，直接查数据库全表过滤
- 向量检索只用于语义模糊匹配（如“知性文艺”）
- 实现“混合检索”策略：精确条件走 DB，模糊描述走向量

### 3.5 SSE 流式连接断开与重试

难点：换用云端模型后，首 token 响应慢（6-15 秒），SSE 连接超时断开，fetch-event-source 自动重试导致死循环。

报错：

```java
java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
```

解决：

- `application.yaml` 增加 `spring.mvc.async.request-timeout: 120000`
- 后端 `onErrorResume` 中判断客户端断开异常，静默处理不打 ERROR 堆栈
- 前端 `fetch-event-source` 的 `onerror` 中 `throw err` 阻止自动重试
- 或改用原生 `fetch + ReadableStream`，完全控制连接生命周期

### 3.6 Ollama 内存不足

报错：`OpenBLAS error: Memory allocation still failed after 10 retries`

原因：`qwen2.5:7b` 加载需 5-8GB 内存，同时运行 IDEA、浏览器等导致内存不足。

解决：

- 重启 Ollama 或重启电脑释放内存
- 卸载不用的模型（`ollama rm <model>`）
- 内存紧张时换用 `qwen2.5:3b`（约 2GB）

### 3.7 推理模型的思考链污染

难点：GLM-5.1 等推理模型输出大量“思考链”token，混入 SSE 流导致前端显示异常，且日志狂刷 `streaming output: null`。

解决：

- 换用非推理模型 `glm-5-turbo`
- 或通过 `enable_thinking: false` 参数关闭思考链
- `application.yaml` 降低 `AgentLlmNode` 日志级别为 WARN

## 四、Ollama 本地模型的局限性

| 局限 | 说明 |
| --- | --- |
| 内存占用大 | qwen2.5:7b 需 5-8GB，qwen2.5:14b 需 10-15GB，普通开发机容易 OOM |
| 响应速度慢 | 本地 CPU/GPU 推理远慢于云端，首 token 延迟 3-10 秒 |
| 模型能力有限 | 7B 模型的工具调用、复杂推理、长文本理解能力弱于云端大模型 |
| 并发能力差 | 本地模型通常只能串行处理请求，多用户同时访问会排队 |
| 思考链模型不友好 | DeepSeek-R1、Qwen3 等推理模型会输出思考链，污染流式输出 |
| 环境依赖 | 需要本地安装 Ollama 并提前 pull 模型，部署复杂 |

## 五、云端模型的问题

### 5.1 响应延迟

| 模型 | 首 token 延迟 | 说明 |
| --- | --- | --- |
| 本地 qwen2.5:7b | 1-3s | 取决于机器性能 |
| 腾讯云 GLM-5.1 | 6-15s | 网络+推理，需调大 SSE 超时 |
| 腾讯云 GLM-5-Turbo | 4-10s | 比 5.1 快，无思考链 |

### 5.2 遇到的问题

- **404 Not Found**：Spring AI 的 `OpenAiApi` 会自动拼接 `/v1`，`base-url` 不能带 `/v1` 后缀
- **403 Forbidden**：API Key 需在控制台绑定对应模型权限
- **思考链污染**：GLM-5.1 输出大量 `null` token，需换 GLM-5-Turbo 或关闭 thinking

### 5.3 模型选型建议

| 场景 | 推荐模型 |
| --- | --- |
| 开发调试 | 本地 qwen2.5:7b（响应快，不花钱） |
| 工具调用测试 | 腾讯云 glm-5-turbo（稳定，无思考链） |
| 上线生产 | 云端大模型 + 本地 fallback |

## 六、快速开始

### 6.1 环境准备

```bash
# 启动 MySQL，导入业务表
# 启动 PostgreSQL + pgvector，创建 partner 表
# 启动 Ollama 并拉取模型
ollama pull qwen2.5:7b
ollama pull nomic-embed-text
```

### 6.2 导入明星数据到 pgvector

通过 `PartnerDataImporter` 将 JSON 数据生成向量存入 pgvector。

数据文件：

- `src/main/resources/female_celebrities_kb.json`（女明星 50+）
- `src/main/resources/male_celebrities_kb.json`（男明星 30+）

### 6.3 启动后端

```bash
mvn clean compile
mvn spring-boot:run
```

服务端口：`http://localhost:1236`

## 七、核心接口

### 心理咨询

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/psychological/stream` | SSE 流式对话 |

### 情感红娘（女性用户→推荐男性）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/male-matchmaker/session/start` | 创建会话 |
| POST | `/api/male-matchmaker/stream` | SSE 流式对话 |
| GET | `/api/male-matchmaker/sessions` | 会话列表 |
| DELETE | `/api/male-matchmaker/sessions/{id}` | 删除会话 |
| GET | `/api/male-matchmaker/sessions/{id}/messages` | 历史消息 |

### 情感红娘（男性用户→推荐女性）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/female-matchmaker/session/start` | 创建会话 |
| POST | `/api/female-matchmaker/stream` | SSE 流式对话 |
| GET | `/api/female-matchmaker/sessions` | 会话列表 |
| DELETE | `/api/female-matchmaker/sessions/{id}` | 删除会话 |
| GET | `/api/female-matchmaker/sessions/{id}/messages` | 历史消息 |

## 八、数据库设计

### MySQL 业务表

| 表名 | 说明 | 关键字段 |
| --- | --- | --- |
| `user` | 用户表 | id, username, gender |
| `consultation_session` | 会话表 | id, user_id, type(PSYCHOLOGICAL/FEMALE_MATCHMAKER/MALE_MATCHMAKER) |
| `consultation_message` | 消息表 | id, session_id, sender_type, content |
| `matchmaker_recommendation` | 推荐记录表 | id, session_id, user_id, partner_id, partner_name |

### PostgreSQL 向量表

| 表名 | 说明 |
| --- | --- |
| `partner` | 明星资料表（id, name, gender, age, zodiac, nationality, tags, interests, personality, appearance, ideal_partner） |
| `vector_store` | Spring AI 自动创建的向量存储表（768维，HNSW 索引，余弦距离） |

## 九、后续优化方向

- 模型降级策略：云端模型超时时自动 fallback 到本地 Ollama
- 缓存层：已推荐明星、历史对话加入 Redis 缓存，减少 DB 查询
- 向量检索优化：为 `partner` 表的 `gender`、`zodiac`、`age` 字段建索引，加速精确过滤
- 提示词优化：针对红娘场景持续调优，减少无关推荐
- 多模态支持：支持明星图片展示，提升用户体验
- 用户画像：记录用户偏好，实现个性化推荐