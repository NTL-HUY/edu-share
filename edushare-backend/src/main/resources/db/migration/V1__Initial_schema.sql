-- ============================================
-- TẠO CÁC BẢNG CƠ SỞ
-- ============================================

-- 1. Bảng users (kế thừa SoftDeleteModel và BaseModel)
CREATE TABLE IF NOT EXISTS users
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    username
        VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    user_role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    -- Constraints
    CONSTRAINT uk_users_username UNIQUE
        (
         username
            ) WHERE deleted_at IS NULL,
    CONSTRAINT uk_users_email UNIQUE
        (
         email
            ) WHERE deleted_at IS NULL,
    CONSTRAINT chk_users_role CHECK
        (
        user_role
            IN
        (
         'USER',
         'ADMIN',
         'INSTRUCTOR'
            ))
);

-- 2. Bảng profiles (kế thừa TimeStampedModel)
CREATE TABLE IF NOT EXISTS profiles
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    user_id
        BIGINT
        NOT
            NULL
        REFERENCES
            users
                (
                 id
                    ) ON DELETE CASCADE,
    student_id VARCHAR(20),
    university VARCHAR(100),
    faculty VARCHAR(100),
    major VARCHAR(100),
    class_name VARCHAR(20),
    academic_year VARCHAR(10),
    cpa DECIMAL(3,
        2),
    bio TEXT,
    cover_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_profiles_user_id UNIQUE
        (
         user_id
            )
);

-- 3. Bảng refresh_tokens (kế thừa BaseModel)
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id
        BIGSERIAL
        PRIMARY
            KEY,
    token_hash
        VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL REFERENCES users
        (
         id
            ) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE
        (
         token_hash
            )
);

-- ============================================
-- TẠO CHỈ MỤC (INDEXES)
-- ============================================

-- Indexes cho bảng users
CREATE INDEX IF NOT EXISTS idx_users_username ON users (username) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_users_user_role ON users (user_role);
CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users (deleted_at);

-- Indexes cho bảng profiles
CREATE INDEX IF NOT EXISTS idx_profiles_user_id ON profiles (user_id);
CREATE INDEX IF NOT EXISTS idx_profiles_student_id ON profiles (student_id);
CREATE INDEX IF NOT EXISTS idx_profiles_university ON profiles (university);
CREATE INDEX IF NOT EXISTS idx_profiles_academic_year ON profiles (academic_year);

-- Indexes cho bảng refresh_tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at ON refresh_tokens (expires_at) WHERE is_revoked = FALSE;
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_revoked ON refresh_tokens (is_revoked);

-- ============================================
-- TẠO CÁC HÀM VÀ TRIGGER
-- ============================================

-- Function: tự động cập nhật updated_at
CREATE
    OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at
        = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

-- Trigger cho bảng users
DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Trigger cho bảng profiles
DROP TRIGGER IF EXISTS update_profiles_updated_at ON profiles;
CREATE TRIGGER update_profiles_updated_at
    BEFORE UPDATE
    ON profiles
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Trigger cho bảng refresh_tokens
DROP TRIGGER IF EXISTS update_refresh_tokens_updated_at ON refresh_tokens;
CREATE TRIGGER update_refresh_tokens_updated_at
    BEFORE UPDATE
    ON refresh_tokens
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- TẠO FUNCTIONS HỖ TRỢ
-- ============================================

-- Function: Lấy thông tin user cùng profile
CREATE
    OR REPLACE FUNCTION get_user_with_profile(p_user_id BIGINT)
    RETURNS TABLE
            (
                user_id BIGINT,
                username VARCHAR,
                email VARCHAR,
                full_name VARCHAR,
                avatar_url VARCHAR,
                user_role VARCHAR,
                student_id VARCHAR,
                university VARCHAR,
                faculty VARCHAR,
                major VARCHAR,
                class_name VARCHAR,
                academic_year VARCHAR,
                cpa DECIMAL
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT u.id,
               u.username,
               u.email,
               u.full_name,
               u.avatar_url,
               u.user_role,
               p.student_id,
               p.university,
               p.faculty,
               p.major,
               p.class_name,
               p.academic_year,
               p.cpa
        FROM users u
                 LEFT JOIN profiles p ON u.id = p.user_id
        WHERE u.id = p_user_id
          AND u.deleted_at IS NULL;
END;
$$
    LANGUAGE plpgsql;

-- Function: Kiểm tra email đã tồn tại
CREATE
    OR REPLACE FUNCTION is_email_exists(p_email VARCHAR)
    RETURNS BOOLEAN AS
$$
BEGIN
    RETURN EXISTS (SELECT 1
                   FROM users
                   WHERE email = p_email
                     AND deleted_at IS NULL);
END;
$$
    LANGUAGE plpgsql;

-- Function: Kiểm tra username đã tồn tại
CREATE
    OR REPLACE FUNCTION is_username_exists(p_username VARCHAR)
    RETURNS BOOLEAN AS
$$
BEGIN
    RETURN EXISTS (SELECT 1
                   FROM users
                   WHERE username = p_username
                     AND deleted_at IS NULL);
END;
$$
    LANGUAGE plpgsql;

-- ============================================
-- TẠO VIEWS HỮU ÍCH
-- ============================================

-- View: User với Profile đầy đủ
CREATE OR REPLACE VIEW v_user_full_info AS
SELECT u.id,
       u.username,
       u.email,
       u.full_name,
       u.avatar_url,
       u.user_role,
       u.created_at AS user_created_at,
       u.updated_at AS user_updated_at,
       p.student_id,
       p.university,
       p.faculty,
       p.major,
       p.class_name,
       p.academic_year,
       p.cpa,
       p.bio,
       p.cover_url
FROM users u
         LEFT JOIN profiles p ON u.id = p.user_id
WHERE u.deleted_at IS NULL;

-- View: Refresh tokens còn hiệu lực
CREATE OR REPLACE VIEW v_active_refresh_tokens AS
SELECT rt.id,
       rt.token_hash,
       rt.expires_at,
       rt.user_id,
       u.username,
       u.email
FROM refresh_tokens rt
         JOIN users u ON rt.user_id = u.id
WHERE rt.is_revoked = FALSE
  AND rt.expires_at > CURRENT_TIMESTAMP
  AND u.deleted_at IS NULL;

-- ============================================
-- SEED DATA MẪU (TÙY CHỌN)
-- ============================================

-- Thêm user admin mặc định (password: admin123)
-- Lưu ý: Password hash được tạo với BCrypt
INSERT INTO users (username, email, password_hash, full_name, user_role)
VALUES ('admin',
        'admin@edushare.com',
        '$2a$10$YourHashedPasswordHere', -- Thay bằng password thật
        'Administrator',
        'ADMIN')
ON CONFLICT (username)
WHERE deleted_at IS NULL DO NOTHING;

-- Thêm user instructor mặc định
INSERT INTO users (username, email, password_hash, full_name, user_role)
VALUES ('instructor',
        'instructor@edushare.com',
        '$2a$10$YourHashedPasswordHere', -- Thay bằng password thật
        'Instructor User',
        'INSTRUCTOR')
ON CONFLICT (username)
WHERE deleted_at IS NULL DO NOTHING;

-- Thêm user thường mặc định
INSERT INTO users (username, email, password_hash, full_name, user_role)
VALUES ('user',
        'user@edushare.com',
        '$2a$10$YourHashedPasswordHere', -- Thay bằng password thật
        'Normal User',
        'USER')
ON CONFLICT (username)
WHERE deleted_at IS NULL DO NOTHING;

-- Thêm profile cho user thường
INSERT INTO profiles (user_id, student_id, university, faculty, major, class_name, academic_year, cpa, bio)
SELECT u.id,
       '2351050055',
       'Đại học Mở TP.HCM',
       'Công nghệ Thông tin',
       'Kỹ thuật Phần mềm',
       'DH23IT01',
       'K23',
       3.62,
       'Sinh viên ngành Kỹ thuật Phần mềm, đam mê lập trình và công nghệ'
FROM users u
WHERE u.username = 'user'
  AND NOT EXISTS (SELECT 1
                  FROM profiles p
                  WHERE p.user_id = u.id);