
-- =====================================================
-- user_feed: bảng fan-out, chỉ ghi cho user thường
-- =====================================================
CREATE TABLE user_feed (
    user_id       BIGINT NOT NULL,
    feed_item_id  BIGINT NOT NULL,
    fanned_out_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, feed_item_id),

    CONSTRAINT fk_user_feed_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,

    CONSTRAINT fk_user_feed_feed_item
        FOREIGN KEY (feed_item_id) REFERENCES feed_item (knowledge_id) ON DELETE CASCADE
);

CREATE INDEX idx_user_feed_user_fanned
    ON user_feed (user_id, fanned_out_at DESC);



CREATE TABLE IF NOT EXISTS comment (
    id                   BIGSERIAL PRIMARY KEY,
    knowledge_id         BIGINT NOT NULL REFERENCES knowledge (id) ON DELETE CASCADE,

    user_id              BIGINT NOT NULL REFERENCES users (id),
    user_name            VARCHAR(100) NOT NULL,
    user_avatar_url      VARCHAR(255),

    -- NULL = comment gốc; khác NULL = reply, luôn trỏ về comment gốc của nhánh
    root_comment_id      BIGINT REFERENCES comment (id) ON DELETE CASCADE,

    -- chỉ phục vụ hiển thị @mention, trỏ đúng dòng user bấm reply
    reply_to_comment_id  BIGINT REFERENCES comment (id) ON DELETE SET NULL,
    reply_to_user_name   VARCHAR(100),

    -- chỉ có ý nghĩa trên comment gốc (reply luôn = 0)
    reply_count          INT NOT NULL DEFAULT 0,

    content              TEXT NOT NULL,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at           TIMESTAMP NULL,
    deleted_by           BIGINT REFERENCES users (id) ON DELETE SET NULL,
    CONSTRAINT chk_comment_reply_has_root
        CHECK (reply_to_comment_id IS NULL OR root_comment_id IS NOT NULL)
);

-- Lấy trang comment gốc, và lấy reply theo từng gốc
CREATE INDEX idx_comment_replies
    ON comment (knowledge_id, root_comment_id, created_at)
    WHERE deleted_at IS NULL AND root_comment_id IS NOT NULL;

-- -- Tra ngược "ai đã reply dòng này" (ít dùng, có thể bỏ nếu không cần)
-- CREATE INDEX idx_comment_reply_to
--     ON comment (reply_to_comment_id)
--     WHERE reply_to_comment_id IS NOT NULL;

-- =====================================================
-- VOTE (upvote/downvote, áp dụng cho cả lesson & question)
-- =====================================================
CREATE TABLE IF NOT EXISTS vote (
    id            BIGSERIAL PRIMARY KEY,
    knowledge_id  BIGINT NOT NULL REFERENCES knowledge (id) ON DELETE CASCADE,
    user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    value         SMALLINT NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_vote_user_knowledge UNIQUE (user_id, knowledge_id),
    CONSTRAINT chk_vote_value CHECK (value IN (-1, 1))
);

-- Tra "user hiện tại đã vote gì cho các item đang hiển thị" (feed/list)
CREATE INDEX idx_vote_user_knowledge
    ON vote (user_id, knowledge_id);

--
-- CREATE TABLE IF NOT EXISTS knowledge_statistics (
--     knowledge_id  BIGINT PRIMARY KEY REFERENCES knowledge (id) ON DELETE CASCADE,
--     views_count   BIGINT NOT NULL DEFAULT 0,
--     created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
-- );