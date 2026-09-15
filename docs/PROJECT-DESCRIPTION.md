# EduShare - Mô tả Project

**EduShare** là nền tảng chia sẻ kiến thức giáo dục toàn diện (full-stack), kết hợp tính năng mạng xã hội học tập với trợ lý AI thông minh.

---

## Tổng quan

EduShare cho phép người dùng tạo, khám phá, thảo luận và trao đổi kiến thức học tập thông qua hai loại nội dung chính:

- **Bài học (Lessons)** — tài liệu học tập có cấu trúc với nội dung Markdown, mức độ khó, thời gian học ước tính, danh mục
- **Câu hỏi (Questions)** — bài đăng thảo luận giáo dục, có thể đánh dấu đã giải quyết

---

## Kiến trúc hệ thống

```
┌──────────────────────┐
│     SvelteKit UI      │
│ Svelte 5 + TypeScript │
└──────────┬───────────┘
           │ GraphQL / HTTP
           ▼
┌──────────────────────┐
│    Spring Boot API   │
│ GraphQL + REST/Auth  │
└──────────┬───────────┘
           │
     ┌─────┼─────┐
     ▼     ▼     ▼
PostgreSQL Redis Kafka
           │
           ▼
┌──────────────────────┐
│    FastAPI RAG       │
│ Chunk / Embed /      │
│ Retrieve / Generate  │
└──────────┬───────────┘
           ▼
     pgvector / Ollama
```

---

## Tech Stack

### Frontend
| Thư viện | Phiên bản |
|----------|-----------|
| SvelteKit | 2.63.0 |
| Svelte | 5.56.1 |
| TypeScript | Project-wide |
| Tailwind CSS | 4.3.0 |
| shadcn-svelte | 1.5.0 |
| GraphQL Codegen | 7.2.0 |

### Backend
| Thư viện | Phiên bản |
|----------|-----------|
| Java | 21 |
| Spring Boot | 3.5.15 |
| Spring Data JPA | Spring-managed |
| Spring GraphQL | Spring-managed |
| PostgreSQL | 16 |
| Kafka | 3.8.0 |
| Redis | 7-alpine |
| Flyway | Spring-managed |
| Cloudinary | 1.39.0 |

### RAG Service
| Thư viện | Phiên bản |
|----------|-----------|
| FastAPI | 0.110.0 |
| Sentence Transformers | Project dependency |
| PyTorch | Project dependency |
| pgvector | Project dependency |
| Ollama | Local LLM |

---

## Cấu trúc project

```
edu-share/
├── edushare-backend/        # Spring Boot API
│   ├── src/main/java/com/nbh/edushare/
│   │   ├── common/          # Chia sẻ DTO, exception, base model
│   │   ├── configs/         # Cấu hình infrastructure
│   │   └── modules/
│   │       ├── auth/        # Xác thực JWT, OAuth
│   │       ├── knowledge/   # CRUD bài học & câu hỏi
│   │       ├── feed/        # Personalized feed, cursor pagination
│   │       ├── interaction/ # Bình luận, vote, bộ đếm
│   │       ├── chat/        # Chat room, tin nhắn
│   │       ├── chatbot/     # Tích hợp RAG/AI
│   │       ├── media/       # Upload media, Cloudinary
│   │       └── user/        # Profile, follows, roles
│   └── src/main/resources/
│       ├── db/migration/    # Flyway migrations
│       └── graphql/         # GraphQL schema
│
├── edushare-frontend/       # SvelteKit UI
│   └── src/
│       ├── routes/          # SvelteKit routes
│       │   ├── (app)/       # Feed, profile, search
│       │   ├── (auth)/      # Login, logout
│       │   ├── api/         # Server endpoints
│       │   └── chat/        # Chat page
│       └── lib/
│           ├── components/  # UI components
│           ├── graphql/     # GraphQL operations
│           ├── generated/   # Auto-generated SDK
│           └── services/    # Service abstractions
│
├── RAG/                     # FastAPI RAG service
│   └── app/
│       ├── main.py          # FastAPI entrypoint
│       ├── chunking.py      # Text splitting
│       ├── embedding.py     # Vector embedding
│       ├── rag.py           # Semantic retrieval
│       ├── listener.py      # Kafka consumer
│       └── db.py            # PostgreSQL + pgvector
│
└── docs/                    # Tài liệu
```

---

## Tính năng chính

### Người dùng
- Đăng ký/đăng nhập bằng email hoặc username
- Google OAuth
- Quản lý profile
- Follow người dùng khác

### Kiến thức
- Tạo/sửa/xóa bài học và câu hỏi
- Phân loại theo danh mục, mức độ khó
- Quyền riêng tư công khai/riêng tư
- Cấu hình quyền bình luận

### Feed
- Personalized feed với cursor-based pagination
- Tìm kiếm theo từ khóa, danh mục, loại nội dung, mức độ khó

### Tương tác
- Bình luận lồng nhau và trả lời
- Upvote/downvote
- Bộ đếm lượt xem, vote, bình luận (Redis)

### Chat
- Chat room cộng đồng
- Gửi, trả lời, xóa tin nhắn
- Theo dõi trạng thái đã đọc/chưa đọc

### AI / RAG
- Semantic search với Vietnamese embedding
- Chatbot trợ lý học tập
- Pipeline event-driven qua Kafka
- Vector storage với pgvector
- Ollama cho local LLM inference

---

## Triển khai

### Yêu cầu
- Java 21+
- Node.js 20+
- Docker & Docker Compose

### Khởi chạy

```bash
# 1. Infrastructure
cd edushare-backend && docker compose up -d

# 2. Backend
./mvnw spring-boot:run

# 3. Frontend
cd ../edushare-frontend && npm install && npm run dev

# 4. RAG Service
cd ../RAG && pip install -r requirements.txt
uvicorn app.main:app --reload
```

### Ports
| Service | Port |
|---------|------|
| Backend | 8080 |
| Frontend | 5173 |
| PostgreSQL | 5432 |
| Kafka | 9092 |
| Redis | 6379 |
| Ollama | 11434 |
