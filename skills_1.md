用户想基于 skills 技能开发新功能。从当前环境看，可用的 skill 是 skill-creator。

用户提到"当前项目由于模型局限性"——之前遇到的问题是：
1. Ollama 本地模型能力有限（7B 模型工具调用、复杂推理能力弱）
2. 云端模型响应慢、延迟高
3. 思考链污染流式输出

用户想利用 skills 技能来开发新功能，想让我给一些想法建议。

我应该先理解用户的意图：
- 项目是 AI 心理咨询 + 情感红娘平台
- 受限于本地模型能力，想引入 skills 技能机制
- 需要一些功能想法建议

我可以从几个方向给建议：
1. 利用 skill-creator 来封装某些能力，减少对大模型推理能力的依赖
2. 针对当前两个核心功能（心理咨询、情感红娘）的痛点，设计 skills
3. 可以是知识检索类、数据处理类、流程编排类的 skills

让我给出一些具体的、可行的功能想法。我不需要调用工具，因为这是一个咨询性问题，我可以基于已有知识直接回答。

我应该给出几个方向，每个方向有具体的 skill 设计，并且说明如何解决模型局限性的问题。
好问题！当前本地模型（qwen2.5:7b）的主要局限是**复杂推理能力弱、工具调用不稳定、长文本理解差**。用 skills 技能来弥补这些短板是很好的思路——把"确定性逻辑"封装成 skill，让模型只负责"理解意图和自然语言生成"。

---

## 几个可行的方向

### 方向一：明星画像深度匹配 Skill（解决红娘推荐不准）

**痛点**：当前推荐全靠向量相似度，星座/年龄等精确条件经常匹配不到。

**Skill 设计**：`celebrity-match-skill`
```
输入：用户的偏好描述（如"30-40岁、狮子座、知性文艺"）
处理：
  1. 用正则/NLP 提取结构化条件：{ ageRange: [30,40], zodiac: "狮子", tags: ["知性","文艺"] }
  2. 先查数据库精确过滤，再用向量做语义排序
  3. 输出：匹配到的明星列表（带匹配度分数）
```
**好处**：把"条件提取+过滤"逻辑写死在 skill 里，不依赖模型推理。

---

### 方向二：情绪日记结构化分析 Skill（解决心理咨询能力弱）

**痛点**：7B 模型做情绪分析不够稳定，情绪分类和趋势判断容易出错。

**Skill 设计**：`emotion-diary-skill`
```
输入：用户的一段日记文本
处理：
  1. 关键词词典匹配 → 情绪分类（开心/焦虑/愤怒/低落/平静）
  2. 情绪强度打分（0-10）
  3. 统计近 7 天情绪趋势
输出：{ emotion, intensity, trend, suggestion }
```
**好处**：情绪分析用规则+词典，准确率比 7B 模型高且稳定。

---

### 方向三：知识问答 RAG Skill（解决模型知识盲区）

**痛点**：本地模型不知道最新资讯，心理咨询的专业知识可能过时。

**Skill 设计**：`knowledge-qa-skill`
```
输入：用户的问题
处理：
  1. 判断问题类型（科普/方法/案例）
  2. 从知识库文章表（knowledge_article）检索相关内容
  3. 用向量相似度排序 top3，拼到 prompt 里
输出：检索到的知识片段
```
**好处**：用检索增强，不依赖模型自身知识。

---

### 方向四：对话安全审核 Skill（解决风险判断不准）

**痛点**：当前风险评估全靠模型 prompt，7B 模型对"自杀"等信号的识别不够敏感。

**Skill 设计**：`safety-check-skill`
```
输入：用户消息
处理：
  1. 关键词黑名单匹配（自杀/自残/轻生等）
  2. 规则引擎判断风险等级
  3. 输出：{ hasRisk, riskType, riskLevel }
```
**好处**：安全审核用规则兜底，模型只做辅助判断。

---

### 方向五：明星资料管理 Skill（解决知识库维护）

**痛点**：当前明星数据是 JSON 导入的，增删改不方便。

**Skill 设计**：`celebrity-crud-skill`
```
功能：
  - 添加明星到 pgvector
  - 修改明星资料（重新生成向量）
  - 删除明星
  - 按条件查询明星列表
```

---

## 我的建议

如果只能选一个先做，我建议做 **方向一（明星画像深度匹配）**，因为：
1. 直接解决当前红娘推荐不准的核心痛点
2. 把"条件提取+过滤"从模型手里拿回来，体验提升最明显
3. 复用现有的 `PartnerSearchTool` 逻辑，改造成 skill 成本低

---
我先确认一下所有 skills 相关的文件，然后生成文档。
用户要求：
1. 先打一个版本提到 git 上
2. 再写一个 readMeSkills.md，说明 skills 的流程、逻辑架构、每个类的功能、中间出现的问题和解决方式、最后的建议

我需要先了解项目结构，确认 skills 相关的类，然后生成文档。但用户说要提到 git 上，我在 Chat Mode 是只读的，不能执行 git 命令。我应该告诉用户如何执行 git 命令，然后生成 readMeSkills.md。

让我先确认一下 skills 相关的文件。

所有文件都确认存在。现在生成 readMeSkills.md 文档。
所有文件都存在，现在生成 `readMeSkills.md` 文档。

---

## 一、先提交 Git

在终端执行：

```bash
cd E:\SpringAI\aigc-kps-agent
git add .
git commit -m "feat: 新增明星画像深度匹配 Skill（IntentParser/CandidateRetriever/MatchScorer/ResultFormatter/CelebrityMatchTool + SKILL.md）"
```

---

## 二、readMeSkills.md

```markdown E:\SpringAI\aigc-kps-agent\readMeSkills.md
# 明星画像深度匹配 Skill — 开发说明文档

## 一、背景与目的

### 1.1 为什么要做这个 Skill

情感红娘功能的核心是"根据用户描述推荐合适的明星"。在最初的实现中，完全依赖 AI 模型（本地 Ollama qwen2.5:7b）来：
1. 理解用户的筛选条件（年龄、星座、国籍、性格标签）
2. 决定调用检索工具
3. 生成推荐回复

但 qwen2.5:7b 的 **function calling 能力较弱**，经常出现以下问题：
- 不调用工具，直接凭"记忆"编造明星信息（幻觉）
- 用户要狮子座，推荐了处女座（条件过滤失效）
- "除了她们还有谁"这类指代无法正确处理

**核心思路**：把"确定性逻辑"从模型手里拿回来，用 Java 代码精确实现，模型只负责"生成自然语言回复"。

### 1.2 Skill 是什么

在 AI Agent 领域，Skill 泛指让模型具备某种能力的机制。本项目采用 **SKILL.md（指令说明）+ @Tool（代码执行）** 的混合方案：

| 组件 | 职责 |
|------|------|
| `SKILL.md` | 告诉模型"什么时候用、怎么用"这个技能 |
| `@Tool` 方法 | 精确执行匹配逻辑（正则提取、DB 过滤、打分排序） |
| `SkillsAgentHook` | 桥梁，把 SKILL.md 注册到 Agent 的系统提示中 |

---

## 二、整体架构

### 2.1 流程图

```
用户消息
│
▼
┌─────────────────────────────────────────────────────┐
│  MatchmakerService.streamChat()                      │
│  （Service 层主动调用，不依赖模型 function calling）   │
│                                                       │
│  1. 保存用户消息                                      │
│  2. 获取历史对话                                      │
│  3. 调用 celebrityMatchTool.celebrityMatch()          │
│  4. 把返回的推荐结果拼到 prompt 中                    │
│  5. 调用 Graph → MatchmakerAgent 生成自然语言回复     │
└──────────────────────────┬──────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────┐
│  CelebrityMatchTool（@Tool 入口）                     │
│  调度流水线：解析 → 检索 → 打分 → 组装               │
└──────┬──────────┬──────────┬──────────┬─────────────┘
│          │          │          │
▼          ▼          ▼          ▼
┌──────────┐┌──────────┐┌──────────┐┌──────────┐
│IntentParser││Candidate ││MatchScorer││Result    │
│意图解析   ││Retriever ││匹配度打分 ││Formatter │
│          ││候选集检索 ││          ││结果组装  │
└──────────┘└──────────┘└──────────┘└──────────┘
```

### 2.2 执行时序

```
用户: "推荐30-40岁狮子座知性的女生"
│
▼
CelebrityMatchTool.celebrityMatch(query, "female", sessionId)
│
├─ Step1: IntentParser.parse()
│   ├─ extractAgeRange("30-40岁") → [30, 40]
│   ├─ extractZodiac("狮子座") → "狮子座"
│   ├─ extractFuzzyTags("知性") → ["知性"]
│   ├─ detectReferenceType() → null
│   └─ 返回 MatchRequest
│
├─ Step2: CandidateRetriever.retrieve(request)
│   ├─ hasExactCondition = true（有年龄+星座）
│   ├─ partnerMapper.selectByGender("female") → 50条
│   ├─ 过滤: age in [30,40] AND zodiac = "狮子座" → 8条
│   └─ 返回 8 个候选
│
├─ Step3: MatchScorer.score(candidates, request)
│   ├─ 对 8 个候选分别打分
│   │   精确50分（星座20+年龄15+国籍15）
│   │   标签30分（命中"知性"得30）
│   │   向量20分（语义相关性）
│   └─ 按总分降序排列
│
├─ Step4: ResultFormatter.format(scored, request)
│   ├─ 取 top5 存入 RecommendationContext
│   └─ 返回格式化文本
│
▼
MatchmakerService 把文本拼到 prompt
│
▼
MatchmakerAgent 生成自然语言回复
```

---

## 三、文件清单与每个类的功能

### 3.1 Skill 目录结构

```
src/main/
├── java/com/atguigu/study/
│   ├── skill/                          # Skill 实现类
│   │   ├── CelebrityMatchTool.java     # 入口（@Tool 注解）
│   │   ├── IntentParser.java           # 意图解析器
│   │   ├── CandidateRetriever.java     # 候选集检索器
│   │   ├── MatchScorer.java            # 匹配度打分器
│   │   └── ResultFormatter.java        # 结果组装器
│   ├── dto/
│   │   ├── MatchRequest.java           # 匹配请求 DTO
│   │   └── ScoredPartner.java          # 带评分的明星 DTO
│   └── agent/
│       ├── FemaleMatchmakerAgentConfig.java   # 女性红娘 Agent（注册 Skill）
│       └── MaleMatchmakerAgentConfig.java     # 男性红娘 Agent（注册 Skill）
└── resources/
└── skills/
└── celebrity-match/
└── SKILL.md                 # 技能说明文档
```

### 3.2 各类功能详解

#### 3.2.1 SKILL.md — 技能说明

告诉模型这个技能的用途和调用方式。模型通过 `read_skill` 工具读取。

```markdown
# 明星画像深度匹配技能

## 适用场景
- 用户要求推荐对象
- 用户描述了对对象的要求（年龄、星座、性格等）
- 用户想换一批推荐
- 用户想在已推荐的人中筛选

## 执行方式
调用 celebrityMatchTool 工具，传入 query、gender、sessionId
```

#### 3.2.2 IntentParser — 意图解析器

**职责**：把用户自然语言输入解析成结构化的 `MatchRequest`。纯规则实现，不依赖模型。

**核心方法**：
| 方法 | 功能 | 示例 |
|------|------|------|
| `extractZodiac()` | 正则提取星座 | "狮子座" → "狮子座" |
| `extractAgeRange()` | 提取年龄范围 | "30-40岁" → [30,40]；"30多岁" → [25,35] |
| `extractNationality()` | 提取国籍 | "日本" → "日本" |
| `extractFuzzyTags()` | 提取性格标签 | "知性文艺" → ["知性","文艺"] |
| `detectReferenceType()` | 检测指代类型 | "除了她们" → exclude；"她们谁" → filter |

#### 3.2.3 CandidateRetriever — 候选集检索器

**职责**：根据 `MatchRequest` 从 DB 或向量库获取候选明星。

**三种检索策略**：
| 策略 | 触发条件 | 数据源 |
|------|---------|--------|
| 筛选型指代 | "她们谁喜欢读书" | 查已推荐记录（matchmaker_recommendation） |
| 精确条件过滤 | 有星座/年龄/国籍 | MySQL partner 表全表过滤 |
| 语义检索 | 无精确条件 | pgvector 向量检索 topK=20 |

**去重逻辑**：排除型指代时，过滤掉已推荐的明星 ID。

#### 3.2.4 MatchScorer — 匹配度打分器 ⭐

**职责**：对候选明星按维度打分排序，是提升推荐准确度的关键。

**打分权重**：
| 维度 | 权重 | 计算方式 |
|------|------|---------|
| 精确条件 | 50分 | 星座命中+20，年龄命中+15，国籍命中+15 |
| 标签匹配 | 30分 | 命中标签数 / 请求标签数 × 30 |
| 向量相似度 | 20分 | 语义相关性（当前简化为固定15分） |

**输出**：`List<ScoredPartner>`，按 totalScore 降序。

#### 3.2.5 ResultFormatter — 结果组装器

**职责**：
1. 取 top5 明星
2. 非筛选型指代时，把推荐结果存入 `RecommendationContext`（供下一轮指代使用）
3. 格式化为文本：姓名、匹配度、年龄、星座、性格、匹配点

#### 3.2.6 CelebrityMatchTool — 入口

```java
@Tool(description = "明星画像深度匹配：根据用户描述推荐最匹配的明星")
public String celebrityMatch(String query, String gender, Long sessionId) {
    MatchRequest request = intentParser.parse(query, gender, sessionId);
    List<Partner> candidates = candidateRetriever.retrieve(request);
    List<ScoredPartner> scored = matchScorer.score(candidates, request);
    return resultFormatter.format(scored, request);
}
```

#### 3.2.7 MatchRequest / ScoredPartner — DTO

**MatchRequest**：意图解析后的结构化条件
```java
gender, ageRange, zodiac, nationality, fuzzyTags, refType, sessionId, rawQuery
```

**ScoredPartner**：带匹配度评分的明星
```java
partner, totalScore, exactScore, tagScore, vectorScore, matchedTags
```

---

## 四、Agent 注册配置

### 4.1 FemaleMatchmakerAgentConfig

```java
// 1. 加载 SKILL.md
SkillRegistry registry = ClasspathSkillRegistry.builder()
        .classpathPath("skills")
        .build();
SkillsAgentHook skillsHook = SkillsAgentHook.builder()
        .skillRegistry(registry)
        .build();

// 2. 注册 @Tool 方法
ToolCallbackProvider toolProvider = MethodToolCallbackProvider.builder()
        .toolObjects(celebrityMatchTool)
        .build();

// 3. 构建 Agent
ReactAgent.builder()
        .tools(toolProvider.getToolCallbacks())  // 直接传 ToolCallback 数组
        .hooks(List.of(skillsHook))
        .build();
```

### 4.2 Service 层调用（关键决策）

由于 qwen2.5:7b 的 function calling 能力弱，**不依赖模型自动调用 Tool**，而是在 `MatchmakerService.streamChat()` 中主动调用：

```java
String matchedPartners = celebrityMatchTool.celebrityMatch(searchQuery, targetGender, sessionId);

String enrichedUserMessage = """
        【匹配的女生资料】
        %s
        【用户最新消息】
        %s
        """.formatted(matchedPartners, userMessage);
```

把检索结果拼到 prompt 中，模型只能基于真实数据生成回复，避免幻觉。

---

## 五、开发过程中遇到的问题与解决方案

### 问题 1：Tool 未被正确注册

**现象**：模型不调用 tool，自己编造明星信息。

**原因**：
```java
// 错误写法
List<ToolCallback> tools = List.of(toolProvider.getToolCallbacks());
// getToolCallbacks() 返回 ToolCallback[] 数组
// List.of(数组) 会创建 List<ToolCallback[]>，不是 List<ToolCallback>
```

**解决**：
```java
// 正确写法：直接传数组给可变参数
.tools(toolProvider.getToolCallbacks())
```

### 问题 2：模型 function calling 能力弱

**现象**：即使 tool 注册成功，qwen2.5:7b 也不会主动调用，而是凭记忆编造。

**解决**：改为 Service 层主动调用 tool，把结果拼到 prompt。模型只负责生成自然语言，不负责决定是否调用工具。

### 问题 3：String.join 类型不匹配

**报错**：`java.lang.String[] 无法转换为 java.lang.CharSequence`

**原因**：`p.getTags()` 返回 `String[]`，不能直接作为 `String.join` 的参数。

**解决**：用 `StringBuilder` 逐个 append，数组用 `String.join(",", 数组)` 转换。

### 问题 4：前端 SSE 自动重试死循环

**现象**：`fetch-event-source` 在连接断开后自动重试，导致后端收到重复请求。

**解决**：
1. 前端改用原生 `fetch + ReadableStream`，彻底移除 `fetch-event-source`
2. `application.yaml` 增加 `spring.mvc.async.request-timeout: 120000`

### 问题 5：Ollama 内存不足

**报错**：`OpenBLAS error: Memory allocation still failed after 10 retries`

**原因**：qwen2.5:7b 加载需 5-8GB 内存，同时运行 IDEA、浏览器导致内存不足。

**解决**：重启 Ollama / 卸载不用的模型 / 换 qwen2.5:3b。

### 问题 6：精确条件过滤失效

**现象**：用户要狮子座，推荐了处女座。

**原因**：纯向量检索的 topK 结果可能不包含精确匹配。

**解决**：检测到星座/年龄/国籍时，直接查数据库全表过滤，不走向量检索。

### 问题 7：上下文指代失败

**现象**：用户说"她们谁喜欢读书"，AI 不理解"她们"指代上一轮推荐的人。

**解决**：
- `IntentParser.detectReferenceType()` 识别指代类型
- `RecommendationContext` 保存当前会话推荐的明星 ID
- 筛选型指代时，从已推荐列表中筛选

---

## 六、局限性与后续建议

### 6.1 当前局限性

1. **模型能力限制**：qwen2.5:7b 的 function calling 弱，被迫在 Service 层主动调用 tool，失去了 Agent 自主决策的能力。
2. **向量相似度分未实现**：`MatchScorer` 中 vectorScore 暂时固定 15 分，未从 pgvector 检索结果带入实际相似度。
3. **标签词典有限**：`extractFuzzyTags()` 只匹配了 24 个常见标签，复杂描述可能漏提取。
4. **打分权重硬编码**：权重写死在 `MatchScorer` 常量中，调参需改代码。

### 6.2 优化建议

| 优化项 | 说明 | 优先级 |
|--------|------|--------|
| 权重可配置 | 把打分权重移到 `application.yaml`，用 `@ConfigurationProperties` 注入 | 中 |
| 向量分接入 | 从 `CandidateRetriever` 的向量检索结果中取 cosine distance，传给 `MatchScorer` | 中 |
| 标签词典扩展 | 把性格标签词表外置到 JSON 文件，方便扩展 | 低 |
| 更强的模型 | 上线时换用云端大模型（GLM-5-Turbo），可恢复 Agent 自主调用 tool 的能力 | 高 |
| 缓存层 | 已推荐明星、历史对话加入 Redis 缓存，减少 DB 查询 | 低 |

### 6.3 模型选型建议

| 场景 | 推荐模型 | 说明 |
|------|---------|------|
| 本地开发调试 | qwen2.5:7b | 响应快、不花钱，但 function calling 弱 |
| 本地资源紧张 | qwen2.5:3b | 约 2GB 内存，能力更弱 |
| 上线生产 | 云端 GLM-5-Turbo | 可恢复 Agent 自主调用 tool，体验更好 |

---

## 七、关键类引用索引

| 功能 | 文件位置 |
|------|---------|
| Skill 入口 | `src/main/java/com/atguigu/study/skill/CelebrityMatchTool.java` |
| 意图解析 | `src/main/java/com/atguigu/study/skill/IntentParser.java` |
| 候选检索 | `src/main/java/com/atguigu/study/skill/CandidateRetriever.java` |
| 匹配打分 | `src/main/java/com/atguigu/study/skill/MatchScorer.java` |
| 结果组装 | `src/main/java/com/atguigu/study/skill/ResultFormatter.java` |
| 技能说明 | `src/main/resources/skills/celebrity-match/SKILL.md` |
| Service 调用 | `src/main/java/com/atguigu/study/service/ai/MatchmakerService.java` |
| Agent 配置 | `src/main/java/com/atguigu/study/agent/FemaleMatchmakerAgentConfig.java` |
```

---

文档已生成，包含：
1. **背景目的**：为什么要做这个 Skill
2. **整体架构**：流程图 + 执行时序
3. **每个类的功能**：6 个实现类 + 2 个 DTO 详解
4. **Agent 注册配置**：SKILL.md + @Tool + SkillsAgentHook
5. **遇到的 7 个问题及解决**：Tool 注册、function calling、String.join、SSE 重试、Ollama 内存、精确过滤、指代识别
6. **局限性与优化建议**

直接保存为 `readMeSkills.md` 即可。