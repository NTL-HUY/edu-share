
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



-- =====================================================
-- CHAT: community group chat (room cố định, seed sẵn, nhiều thành viên)
-- =====================================================

CREATE TABLE IF NOT EXISTS chat_room (
    id BIGSERIAL PRIMARY KEY,
--     category_id BIGINT REFERENCES category(id),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
--     avatar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,  -- admin có thể tắt room mà không cần xóa
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- room_member: trạng thái của user đối với 1 room
-- (đã join hay chưa + con trỏ đã đọc tới đâu, dùng để tính unread badge)
-- =====================================================
CREATE TABLE IF NOT EXISTS room_read_state (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    last_read_message_id BIGINT NULL,
    last_visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_room_read_state_room_user UNIQUE (room_id, user_id)
);

CREATE INDEX idx_room_read_state_user ON room_read_state (user_id);


-- =====================================================
-- chat_message: tin nhắn trong room, có reply (không nested, chỉ trỏ thẳng 1 cấp)
-- =====================================================
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGSERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES chat_room(id) ON DELETE CASCADE,
    client_temp_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    user_name VARCHAR(100) NOT NULL,     -- denormalize, tránh join lúc render list
    user_avatar_url VARCHAR(255),

    reply_to_message_id BIGINT REFERENCES chat_message(id) ON DELETE SET NULL,
    reply_to_user_name VARCHAR(100),               -- snapshot, phòng tin gốc bị xóa
    reply_to_content_preview VARCHAR(200),             -- snapshot ngắn nội dung tin gốc

    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    deleted_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uk_chat_message_client_temp_id_sender
    UNIQUE (client_temp_id, user_id)
);

-- Phân trang tin nhắn theo room, mới nhất trước (giống pattern feed_item)
CREATE INDEX idx_chat_message_room_created
    ON chat_message (room_id, id DESC)
    WHERE deleted_at IS NULL;

-- Tính unread badge: COUNT(*) WHERE room_id=? AND id > last_read_message_id
-- Index trên đã đủ phục vụ query dạng room_id + id range, không cần thêm index riêng.