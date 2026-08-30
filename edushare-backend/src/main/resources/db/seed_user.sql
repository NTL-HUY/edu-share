-- db/seed_user.sql

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =====================================================
-- USERS
-- =====================================================
INSERT INTO users (username, email, password_hash, full_name, user_role) VALUES
('admin', 'admin@edushare.vn', crypt('Admin@123', gen_salt('bf', 10)), 'Quản trị viên', 'ADMIN');

INSERT INTO users (username, email, password_hash, full_name, user_role)
SELECT
    'user' || i,
    'user' || i || '@edushare.vn',
    crypt('User@123', gen_salt('bf', 10)),
    'Người dùng ' || i,
    'USER'
FROM generate_series(1, 20) AS i;

-- =====================================================
-- PROFILES (tên bảng: profiles, không có cột student_id? có, giữ nguyên)
-- =====================================================
INSERT INTO profiles (user_id, student_id, university, faculty, major, class_name, academic_year, cpa, bio)
SELECT
    u.id,
    '235105' || lpad((floor(random() * 9999))::int::text, 4, '0'),
    (ARRAY['Đại học Mở TP.HCM','Đại học Bách Khoa TP.HCM','Đại học Khoa học Tự nhiên',
           'Đại học Kinh tế TP.HCM','Đại học Công nghệ Thông tin'])[floor(random() * 5 + 1)],
    (ARRAY['Công nghệ Thông tin','Kỹ thuật Phần mềm','Khoa học Máy tính',
           'Kinh tế','Quản trị Kinh doanh'])[floor(random() * 5 + 1)],
    'Kỹ thuật Phần mềm',
    'DH23IT0' || (floor(random() * 9 + 1))::int,
    'K23',
    round((2.5 + random() * 1.5)::numeric, 2),
    'Xin chào, mình là ' || u.full_name
FROM users u;

-- =====================================================
-- FOLLOWS (tên bảng: follows)
-- =====================================================
WITH follower_limits AS (
    SELECT id AS follower_id, (3 + floor(random() * 4))::int AS follow_limit
    FROM users WHERE user_role = 'USER'
),
ranked AS (
    SELECT
        fl.follower_id,
        e.id AS followee_id,
        fl.follow_limit,
        row_number() OVER (PARTITION BY fl.follower_id ORDER BY random()) AS rn
    FROM follower_limits fl
    JOIN users e ON e.id != fl.follower_id AND e.user_role = 'USER'
)
INSERT INTO follows (follower_id, followee_id)
SELECT follower_id, followee_id
FROM ranked
WHERE rn <= follow_limit;