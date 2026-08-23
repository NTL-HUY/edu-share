CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE TABLE IF NOT EXISTS knowledge (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    abstract VARCHAR(500),
    thumbnail_url VARCHAR(500),
    owner_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT REFERENCES category(id),
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    allow_comment BOOLEAN NOT NULL DEFAULT TRUE,
    views_count INT NOT NULL DEFAULT 0,
    vote_score INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    deleted_by BIGINT NULL REFERENCES users(id),

    constraint chk_knowledge_type check (type in ('LESSON', 'QUESTION'))
);

CREATE INDEX idx_knowledge_owner_id ON knowledge (owner_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_knowledge_type ON knowledge (type) WHERE deleted_at IS NULL;
CREATE INDEX idx_knowledge_category_id ON knowledge (category_id) WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS lesson (
    knowledge_id BIGINT PRIMARY KEY REFERENCES knowledge(id) ON DELETE CASCADE,
    content_markdown TEXT,
--     toc_json JSONB,
--     video_url VARCHAR(500),
    level VARCHAR(20),
--     is_preview BOOLEAN NOT NULL DEFAULT FALSE,
    estimate_time_in_minutes INT,
--     category_id BIGINT REFERENCES category(id),
--     order_index INT,

    CONSTRAINT chk_lesson_level CHECK (level IS NULL OR level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);


CREATE TABLE IF NOT EXISTS question (
    knowledge_id BIGINT PRIMARY KEY REFERENCES knowledge(id) ON DELETE CASCADE,
    content TEXT,
--     category_id BIGINT REFERENCES category(id),
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    accepted_answer_id BIGINT NULL
);


CREATE TABLE IF NOT EXISTS feed_item (
    knowledge_id BIGINT PRIMARY KEY REFERENCES knowledge(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    owner_avatar_url VARCHAR(500),
    title VARCHAR(255) NOT NULL,
    abstract VARCHAR(500),
    thumbnail_url VARCHAR(500),
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    allow_comment BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT,
    category_name VARCHAR(100),
    views_count INT NOT NULL DEFAULT 0,
    vote_score INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    type_meta JSONB,
    source_created_at TIMESTAMP NOT NULL,
    synced_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT chk_feed_item_type CHECK (type IN ('LESSON', 'QUESTION'))
);

CREATE INDEX idx_feed_item_owner_created
    ON feed_item (owner_id, source_created_at DESC, knowledge_id DESC);

CREATE INDEX idx_feed_item_created
    ON feed_item (source_created_at DESC, knowledge_id DESC)
    WHERE deleted_at IS NULL AND is_public = TRUE;

-- Cho search filter theo type
CREATE INDEX idx_feed_item_type ON feed_item (type) WHERE deleted_at IS NULL;

-- Cho search filter theo category
CREATE INDEX idx_feed_item_category ON feed_item (category_id) WHERE deleted_at IS NULL;

-- Cho search filter theo level (nằm trong JSONB)
CREATE INDEX idx_feed_item_level ON feed_item ((type_meta->>'level')) WHERE deleted_at IS NULL;

--
-- -- Full-text search (đã nói ở tin trước)
-- ALTER TABLE feed_item ADD COLUMN search_vector tsvector
--     GENERATED ALWAYS AS (to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(abstract,''))) STORED;
-- CREATE INDEX idx_feed_item_search_vector ON feed_item USING GIN (search_vector);


-- Bật extension pgvector (chạy 1 lần, cần quyền superuser hoặc user có quyền CREATE EXTENSION)
CREATE EXTENSION IF NOT EXISTS vector;

-- =====================================================
-- knowledge_chunk: bảng lưu chunk + embedding, dùng cho RAG
-- Đây là bảng "read model" riêng của Python service, giống tinh thần
-- fan-out của feed_item bên Spring: denormalize sẵn owner_id/is_public
-- để lúc retrieval khỏi phải join sang DB gốc.
-- =====================================================
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id              BIGSERIAL PRIMARY KEY,
    knowledge_id    BIGINT NOT NULL,
    knowledge_type  VARCHAR(20) NOT NULL,   -- LESSON | QUESTION
    owner_id        BIGINT NOT NULL,
    is_public       BOOLEAN NOT NULL DEFAULT TRUE,
    title           VARCHAR(255) NOT NULL,
    chunk_index     INT NOT NULL,
    content         TEXT NOT NULL,
    embedding       vector(768) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_knowledge_chunk_idx UNIQUE (knowledge_id, chunk_index),
    CONSTRAINT chk_knowledge_chunk_type CHECK (knowledge_type IN ('LESSON', 'QUESTION'))
);

-- Xoá hết chunk theo knowledge_id (khi UPDATED/DELETED)
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_knowledge_id
    ON knowledge_chunk (knowledge_id);

-- Vector search (HNSW, cosine distance)
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_embedding
    ON knowledge_chunk USING hnsw (embedding vector_cosine_ops);

-- Lọc nhanh theo quyền xem trước khi tính khoảng cách vector
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_visibility
    ON knowledge_chunk (is_public, owner_id);