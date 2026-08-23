-- 1. Bảng users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    is_famous BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url VARCHAR(255),
    user_role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    CONSTRAINT chk_users_role CHECK (user_role IN ('USER', 'ADMIN'))
);

-- Partial Unique Indexes (Cho phép Unique nhưng bỏ qua các bản ghi đã xóa soft-delete)
CREATE UNIQUE INDEX uk_users_username ON users (username) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX uk_users_email ON users (email) WHERE deleted_at IS NULL;

-- 2. Bảng profiles (kế thừa TimeStampedModel)
CREATE TABLE IF NOT EXISTS profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    student_id VARCHAR(20),
    university VARCHAR(100),
    faculty VARCHAR(100),
    major VARCHAR(100),
    class_name VARCHAR(20),
    academic_year VARCHAR(10),
    cpa DECIMAL(3, 2),
    bio TEXT,
    cover_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_profiles_user_id UNIQUE (user_id)
);

-- 3. Bảng refresh_tokens (kế thừa BaseModel)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash)
);


CREATE TABLE follows (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_followee FOREIGN KEY (followee_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_follow_pair UNIQUE (follower_id, followee_id),
    CONSTRAINT chk_follow_not_self CHECK (follower_id <> followee_id)
);

CREATE INDEX idx_follow_followee ON follows (followee_id);