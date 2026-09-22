
根据 `Partner.java` 实体和 `application.yaml` 配置，生成 PostgreSQL 建表文档：

```sql E:\SpringAI\aigc-kps-agent\docs\postgresql_setup.sql
-- ============================================================
-- PostgreSQL + pgvector 建表脚本
-- 数据库：matchmaker
-- 说明：明星资料表（partner）+ 向量存储表（vector_store）
-- ============================================================

-- 1. 启用 pgvector 扩展（必须先安装 postgresql15-pgvector）
CREATE EXTENSION IF NOT EXISTS vector;

-- ============================================================
-- 2. 明星资料表（partner）
--    存储结构化的明星信息，用于精确过滤（星座/年龄/国籍等）
-- ============================================================
CREATE TABLE IF NOT EXISTS partner (
    id              VARCHAR(64)   PRIMARY KEY,
    name            VARCHAR(100)  NOT NULL,
    name_en         VARCHAR(100),
    category        VARCHAR(50),
    gender          VARCHAR(10),
    nationality     VARCHAR(50),
    birth_year      INTEGER,
    age             INTEGER,
    zodiac          VARCHAR(20),
    blood_type      VARCHAR(10),
    height_cm       INTEGER,
    weight_kg       INTEGER,
    ideal_partner   TEXT,
    bio             TEXT,
    appearance      JSONB,              -- 外貌特征（Map<String,String>）
    personality     JSONB,              -- 性格标签数组
    interests       JSONB,              -- 兴趣爱好数组
    tags            JSONB,              -- 标签数组
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引：加速精确过滤
CREATE INDEX IF NOT EXISTS idx_partner_gender    ON partner (gender);
CREATE INDEX IF NOT EXISTS idx_partner_zodiac    ON partner (zodiac);
CREATE INDEX IF NOT EXISTS idx_partner_age       ON partner (age);
CREATE INDEX IF NOT EXISTS idx_partner_nationality ON partner (nationality);

-- 注释
COMMENT ON TABLE partner IS '明星资料表';
COMMENT ON COLUMN partner.id IS '明星ID';
COMMENT ON COLUMN partner.name IS '中文名';
COMMENT ON COLUMN partner.name_en IS '英文名';
COMMENT ON COLUMN partner.gender IS '性别 female/male';
COMMENT ON COLUMN partner.nationality IS '国籍';
COMMENT ON COLUMN partner.zodiac IS '星座';
COMMENT ON COLUMN partner.personality IS '性格标签 JSON 数组';
COMMENT ON COLUMN partner.interests IS '兴趣爱好 JSON 数组';
COMMENT ON COLUMN partner.tags IS '标签 JSON 数组';


-- ============================================================
-- 3. 向量存储表（vector_store）
--    Spring AI pgvector 自动管理，也可手动创建
--    配置见 application.yaml:
--      index-type: HNSW
--      distance-type: COSINE_DISTANCE
--      dimensions: 768
--      initialize-schema: true  → 启动时自动创建
-- ============================================================
CREATE TABLE IF NOT EXISTS vector_store (
    id          UUID PRIMARY KEY,
    content     TEXT,
    metadata    JSONB,
    embedding   vector(768)
);

-- HNSW 索引（余弦距离）
CREATE INDEX IF NOT EXISTS idx_vector_store_embedding
    ON vector_store
    USING hnsw (embedding vector_cosine_ops);


-- ============================================================
-- 4. 验证
-- ============================================================
-- 查看表
\dt

-- 查看 pgvector 版本
SELECT extname, extversion FROM pg_extension WHERE extname = 'vector';

-- 查看向量维度
SELECT array_length(embedding, 1) FROM vector_store LIMIT 1;
```

---

## 配置说明

### application.yaml 中 pgvector 相关配置

```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW            # 索引类型：HNSW（近似最近邻搜索）
        distance-type: COSINE_DISTANCE  # 距离度量：余弦距离
        dimensions: 768             # 向量维度（nomic-embed-text 输出 768 维）
        initialize-schema: true     # 启动时自动创建 vector_store 表

# PostgreSQL 独立数据源
pg:
  datasource:
    jdbc-url: jdbc:postgresql://39.96.42.202:5432/matchmaker
    username: postgres
    password: 123456
    driver-class-name: org.postgresql.Driver
```

### 关键参数说明

| 参数 | 值 | 说明 |
|------|-----|------|
| `index-type` | HNSW | 近似最近邻索引，比 IVFFlat 更快，无需训练 |
| `distance-type` | COSINE_DISTANCE | 余弦距离，适合语义相似度 |
| `dimensions` | 768 | nomic-embed-text 模型输出维度 |
| `initialize-schema` | true | Spring Boot 启动时自动创建 `vector_store` 表 |

---

## 使用说明

1. **先执行 SQL**：在 PostgreSQL 的 `matchmaker` 数据库中执行上面的 SQL
2. **启动应用**：`initialize-schema: true` 会自动管理 `vector_store` 表
3. **导入数据**：通过 `PartnerDataImporter` 将 JSON 数据导入 `partner` 表并生成向量存入 `vector_store`

`partner` 表需要手动创建（因为 Spring AI 只管 `vector_store`），`vector_store` 表可以让 Spring AI 自动创建，也可以手动执行上面的 SQL。