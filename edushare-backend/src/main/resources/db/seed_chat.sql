-- db/seed_chat.sql
-- =====================================================
-- CHAT_ROOM
-- =====================================================
INSERT INTO chat_room (name, description, is_active)
SELECT v.name, v.description, v.is_active
FROM (VALUES
    ('Thảo luận chung', 'Nơi trao đổi chung của cộng đồng', TRUE),
    ('Hỏi đáp Toán', 'Hỏi bài, thảo luận Toán học', TRUE),
    ('Hỏi đáp Lý', 'Hỏi bài, thảo luận Vật lý', TRUE),
    ('Góc chia sẻ tài liệu', 'Chia sẻ tài liệu, đề thi', TRUE),
    ('Room đã đóng (test)', 'Room này để test is_active = false', FALSE)
) AS v(name, description, is_active)
WHERE NOT EXISTS (
    SELECT 1 FROM chat_room c WHERE c.name = v.name
);

-- =====================================================
-- CHAT_MESSAGE: 2 tin đầu cho room "Thảo luận chung"
-- =====================================================
INSERT INTO chat_message (room_id, user_id, user_name, content, client_temp_id)
SELECT r.id, u.id, u.username, 'Chào mọi người!', gen_random_uuid()::text
FROM chat_room r, users u
WHERE r.name = 'Thảo luận chung'
  AND u.username = 'admin'
  AND NOT EXISTS (SELECT 1 FROM chat_message m WHERE m.room_id = r.id);

INSERT INTO chat_message (room_id, user_id, user_name, content, client_temp_id)
SELECT r.id, u.id, u.username, 'Room này để bàn chuyện gì vậy?', gen_random_uuid()::text
FROM chat_room r, users u
WHERE r.name = 'Thảo luận chung'
  AND u.username = 'user1'
  AND NOT EXISTS (
      SELECT 1 FROM chat_message m
      WHERE m.room_id = r.id AND m.content = 'Room này để bàn chuyện gì vậy?'
  );

INSERT INTO chat_message (
    room_id, user_id, user_name, content,
    reply_to_message_id, reply_to_user_name, reply_to_content_preview, client_temp_id
)
SELECT r.id, u.id, u.username, 'Bàn chuyện chung thôi bạn ơi',
       m.id, m.user_name, m.content, gen_random_uuid()::text
FROM chat_room r
JOIN users u ON u.username = 'admin'
JOIN chat_message m ON m.room_id = r.id AND m.content = 'Room này để bàn chuyện gì vậy?'
WHERE r.name = 'Thảo luận chung'
  AND NOT EXISTS (
      SELECT 1 FROM chat_message x
      WHERE x.room_id = r.id AND x.reply_to_message_id = m.id
  );

INSERT INTO chat_message (room_id, user_id, user_name, content, client_temp_id)
SELECT r.id, u.id, u.username, 'Ai giải giúp mình bài đạo hàm này với', gen_random_uuid()::text
FROM chat_room r, users u
WHERE r.name = 'Hỏi đáp Toán'
  AND u.username = 'user2'
  AND NOT EXISTS (SELECT 1 FROM chat_message m WHERE m.room_id = r.id);