# EduShare - Educational Knowledge-Sharing Platform

**EduShare** is a full-stack educational social platform designed to help users publish, discover, discuss, and exchange learning knowledge through lessons and questions. Built with **SvelteKit, Svelte 5, Tailwind CSS, Spring Boot, GraphQL, PostgreSQL, Kafka, Redis, and a dedicated FastAPI RAG service**, the platform combines a modern learning-oriented interface with a scalable, event-driven backend architecture.

---

# Overview

EduShare is an educational knowledge-sharing platform that combines the characteristics of a social network, collaborative learning community, and intelligent knowledge assistant.

The platform allows users to create and consume two major types of educational content:

* **Lessons** — structured learning materials containing Markdown content, difficulty levels, estimated learning time, categories, and optional thumbnails.
* **Questions** — discussion-oriented educational posts that can be marked as resolved and can contain an accepted answer.

Both content types are represented under a common **Knowledge** domain, allowing shared capabilities such as feed distribution, comments, voting, views, ownership, and visibility. The GraphQL schema exposes `Lesson` and `Question` as implementations of the shared `Knowledge` interface.

The platform also provides:

* Personalized knowledge feeds
* Knowledge search
* User profiles and follow relationships
* Nested comments and replies
* Upvote/downvote interactions
* Community chat rooms
* Read/unread chat state
* Media upload
* Secure authentication
* AI-powered semantic retrieval and chatbot assistance

The backend is organized as a **modular monolith / micro-modular architecture**, where each business domain maintains its own controllers, services, DTOs, repositories, events, and supporting infrastructure.

---

# Core Features

## 👤 User Features

### **Account Registration & Login**

Users can register and authenticate using username/email and password through the authentication module.

### **Google OAuth**

The authentication system also supports Google OAuth through Authlib on the backend.

### **Profile Management**

Users can maintain profile information such as:

* Username
* Full name
* Avatar
* Academic information
* Personal biography

### **Follow System**

Users can follow other users and maintain follower/following relationships.

### **User Activity**

Profiles expose user-generated knowledge and interaction-related activity, including lessons, questions, comments, votes, followers, and following relationships.

The frontend profile experience is organized around tabs such as `profile`, `activity`, `saves`, and `settings`.

---

# 📚 Knowledge Features

## **Lesson Publishing**

Users can create lessons containing:

* Title
* Abstract
* Thumbnail
* Markdown content
* Category
* Difficulty level
* Estimated learning time
* Public/private visibility
* Comment permission

## **Question Publishing**

Users can create questions containing:

* Title
* Abstract
* Content
* Category
* Visibility
* Comment permission
* Resolution state

## **Knowledge Editing**

Existing lessons and questions can be updated while retaining their type-specific properties.

## **Knowledge Deletion**

The backend exposes knowledge deletion through the GraphQL mutation:

```text
deleteKnowledge(id: ID!)
```

## **My Content**

Users can query their own knowledge content with pagination and sorting.

## **Knowledge Detail**

Each knowledge item exposes:

* Owner
* Category
* Views
* Vote score
* Comment count
* Visibility
* Type-specific content
* Current user's vote
* Comments

---

# 📰 Feed Features

## **Personalized Feed**

The feed module provides a personalized stream of knowledge items from users and followed content sources.

### **Cursor-Based Pagination**

The feed uses cursor-based pagination:

```text
Feed
├── items
├── nextCursor
└── hasMore
```

This avoids relying exclusively on traditional offset-based pagination for deep feed traversal.

### **Feed Search**

Users can search the feed by:

* Keyword
* Knowledge type
* Category
* Lesson level
* Sorting
* Page size

### **Feed Projection**

The backend separates the read-oriented feed representation from the original knowledge domain using projection entities such as:

```text
FeedItem
UserFeed
LessonFeedMeta
QuestionFeedMeta
```

---

# 💬 Interaction Features

## **Nested Comments**

The platform supports:

* Root comments
* Replies
* Reply-to-comment references
* Reply counts
* Comment pagination

The comment model stores both root and direct reply relationships.

### **Comment Pagination**

Comments support cursor-based pagination:

```text
items
nextCursor
hasMore
```

### **Comment Moderation Metadata**

Comments can contain:

* `deletedAt`
* `deletedBy`
* `updatedAt`

allowing the system to represent deleted or modified comments.

---

## **Voting**

Users can interact with knowledge through:

* Upvote
* Downvote
* Current user vote state
* Aggregate vote score

The frontend interaction layer reflects vote state directly in the UI.

---

## **Interaction Counters**

The system tracks high-frequency counters including:

* Views
* Vote score
* Comment count

These counters are separated from the main interaction operations and supported by a dedicated counter subsystem.

---

# 💬 Community Chat

## **Chat Rooms**

Users can access community-based chat rooms represented by:

```text
ChatRoom
├── id
├── name
├── description
└── unreadCount
```

## **Message Management**

The chat module supports:

* Sending messages
* Replying to messages
* Reading messages
* Deleting messages
* Cursor-based message pagination

## **Unread Message Tracking**

The system stores the last read message state for each user and room, allowing the frontend to display unread counts.

The database indexes chat messages by room and message ID to support efficient retrieval and unread-count queries.

> The current codebase provides the chat domain and GraphQL mutation/query infrastructure. A dedicated WebSocket or GraphQL subscription transport is not exposed in the reviewed source, so the project should not be described as having a completed WebSocket-based chat layer.

---

# 🤖 AI Assistant & RAG

## **AI Knowledge Assistant**

EduShare includes a separate **FastAPI-based RAG service** designed to provide semantic knowledge retrieval and AI-generated answers.

The RAG service uses:

* Sentence Transformers
* Vietnamese embedding model
* PostgreSQL + pgvector
* Kafka
* Ollama
* LangChain text splitters

## **Vietnamese Semantic Embeddings**

The service uses:

```text
bkai-foundation-models/vietnamese-bi-encoder
```

to transform Vietnamese educational content into vector embeddings.

The embedding pipeline generates normalized embeddings through Sentence Transformers.

## **Knowledge Chunking**

Lessons are split using Markdown heading-aware splitting before recursive chunking.

Questions combine:

```text
Title
+
Abstract
+
Content
```

before applying recursive text splitting. The configured chunk size is `1000` characters with `150` characters of overlap.

## **Event-Driven Embedding Pipeline**

The RAG service consumes knowledge events from Kafka:

```text
Spring Boot
     │
     │ Knowledge Event
     ▼
   Kafka
     │
     ▼
RAG Listener
     │
     ▼
Chunking
     │
     ▼
Embedding
     │
     ▼
pgvector
```

The listener consumes the `knowledge-created` Kafka topic and processes create/update/delete knowledge events.

## **Semantic Retrieval**

The service converts a user query into an embedding and searches similar chunks using vector similarity.

Configured retrieval parameters include:

```text
Top K:            6
Minimum Similarity: 0.35
Chunk Size:       1000
Chunk Overlap:    150
```

## **Privacy-Aware Retrieval**

The `knowledge_chunk` read model stores:

* `knowledge_id`
* `knowledge_type`
* `owner_id`
* `is_public`
* `title`
* `content`
* `embedding`

The retrieval query only allows:

```text
Public knowledge
        OR
Knowledge owned by the current/visible user
```

This allows access-control filtering to be applied directly to the vector read model instead of requiring every retrieval request to reconstruct visibility from the original knowledge domain.

---

# 🔐 Authentication & Security

## **JWT Authentication**

The backend issues signed JWT access tokens containing:

* User ID
* User role
* Issue time
* Expiration time

The token implementation uses **HS256** signing through Nimbus JOSE JWT.

## **Access & Refresh Tokens**

The authentication flow generates both:

```text
Access Token
+
Refresh Token
```

and the refresh flow rotates the refresh token before generating a new access token.

## **Role-Based Authorization**

The user domain defines role information, including:

```text
ADMIN
USER
```

The JWT payload also carries the user's role, allowing Spring Security to use role-aware authentication.

## **Secure Frontend Session Handling**

The SvelteKit frontend stores authentication state using secure cookies configured with:

```text
httpOnly
secure
sameSite=strict
```

The server-side SvelteKit layer also intercepts failed authenticated requests and can retry requests after refreshing the access token.

---

# Technology Stack

## Frontend

| Category        | Technology               |               Version |
| --------------- | ------------------------ | --------------------: |
| Framework       | **SvelteKit**            |              `2.63.0` |
| UI Framework    | **Svelte**               |              `5.56.1` |
| Language        | **TypeScript**           |          Project-wide |
| Build Tool      | **Vite**                 | SvelteKit integration |
| Styling         | **Tailwind CSS**         |               `4.3.0` |
| UI Components   | **shadcn-svelte**        |               `1.5.0` |
| Icons           | **Lucide Svelte**        |              `1.33.0` |
| GraphQL Client  | **graphql-request**      |         Generated SDK |
| GraphQL Codegen | **@graphql-codegen/cli** |               `7.2.0` |
| Validation      | **Zod**                  |    Project dependency |
| Formatting      | **Prettier**             |               `3.8.3` |
| Linting         | **ESLint**               |              `10.4.1` |

The frontend scripts include GraphQL code generation, development, production build, type checking, linting, and formatting workflows.

---

## Backend

| Category              | Technology                      |             Version |
| --------------------- | ------------------------------- | ------------------: |
| Runtime               | **Java**                        |                `21` |
| Framework             | **Spring Boot**                 |            `3.5.15` |
| ORM                   | **Spring Data JPA / Hibernate** |      Spring-managed |
| API                   | **Spring for GraphQL**          | Spring Boot managed |
| Security              | **Spring Security**             | Spring Boot managed |
| JWT                   | **Nimbus JOSE JWT**             |              `10.1` |
| Mapping               | **MapStruct**                   |       `1.5.5.Final` |
| Boilerplate Reduction | **Lombok**                      |       Maven-managed |
| Database              | **PostgreSQL**                  |                `16` |
| Migration             | **Flyway**                      |      Spring-managed |
| Messaging             | **Spring Kafka / Apache Kafka** |       Kafka `3.8.0` |
| Cache                 | **Redis**                       |          `7-alpine` |
| Media                 | **Cloudinary**                  |   Java SDK `1.39.0` |

## The backend Maven configuration explicitly uses Spring Boot `3.5.15`, Java `21`, Nimbus `10.1`, MapStruct `1.5.5.Final`, Cloudinary `1.39.0`, Spring Kafka, Redis, and Flyway.

## RAG Service

| Category          | Technology                   |            Version |
| ----------------- | ---------------------------- | -----------------: |
| Framework         | **FastAPI**                  |          `0.110.0` |
| ASGI Server       | **Uvicorn**                  |           `0.28.0` |
| Embeddings        | **Sentence Transformers**    | Project dependency |
| ML Runtime        | **PyTorch**                  | Project dependency |
| Validation        | **Pydantic**                 | Project dependency |
| Kafka Client      | **aiokafka**                 | Project dependency |
| Text Splitting    | **LangChain Text Splitters** | Project dependency |
| PostgreSQL Driver | **asyncpg**                  | Project dependency |
| Vector Search     | **pgvector**                 | Project dependency |
| HTTP Client       | **Requests**                 | Project dependency |

---

# Infrastructure & Tools

The local infrastructure is containerized through Docker Compose.

```text
PostgreSQL + pgvector
        │
        ├── Application Database
        └── RAG Vector Store

Kafka
        │
        ├── Knowledge Events
        └── Asynchronous Processing

Redis
        │
        └── Interaction Counters

Kafka UI
        │
        └── Event Monitoring

Ollama
        │
        └── Local LLM Inference
```

The backend Compose configuration provides PostgreSQL/pgvector, Kafka, Kafka UI, and Redis containers.
The RAG environment separately provides an Ollama service on port `11434`.

---

# Project Structure

```text
ntl-huy-edu-share/
│
├── README.md
│
├── edushare-backend/
│   ├── pom.xml
│   ├── docker-compose.yaml
│   ├── .env.example
│   ├── mvnw
│   ├── mvnw.cmd
│   │
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/nbh/edushare/
│       │   │       │
│       │   │       ├── common/
│       │   │       ├── configs/
│       │   │       │
│       │   │       └── modules/
│       │   │           ├── auth/
│       │   │           ├── chat/
│       │   │           ├── chatbot/
│       │   │           ├── feed/
│       │   │           ├── interaction/
│       │   │           ├── knowledge/
│       │   │           ├── media/
│       │   │           └── user/
│       │   │
│       │   └── resources/
│       │       ├── application.yaml
│       │       ├── db/
│       │       │   ├── migration/
│       │       │   └── seed_*.sql
│       │       └── graphql/
│       │           ├── common.graphql
│       │           └── module/
│       │
│       └── test/
│           └── java/
│
├── edushare-frontend/
│   ├── package.json
│   ├── codegen.yml
│   ├── tsconfig.json
│   ├── vite.config.ts
│   ├── graphql.config.yml
│   │
│   └── src/
│       ├── hooks.server.ts
│       ├── lib/
│       │   ├── auth/
│       │   ├── components/
│       │   ├── configs/
│       │   ├── constants/
│       │   ├── generated/
│       │   ├── graphql/
│       │   ├── schemas/
│       │   ├── services/
│       │   ├── stores/
│       │   ├── styles/
│       │   ├── types/
│       │   └── utils/
│       │
│       └── routes/
│           ├── (app)/
│           │   ├── feed/
│           │   ├── profile/
│           │   └── search/
│           ├── (auth)/
│           │   ├── login/
│           │   └── logout/
│           ├── api/
│           ├── chat/
│           ├── my-content/
│           └── register/
│
└── RAG/
    ├── docker-compose.yml
    ├── Dockerfile
    ├── requirements.txt
    │
    └── app/
        ├── chunking.py
        ├── config.py
        ├── db.py
        ├── download_model.py
        ├── dto.py
        ├── embedding.py
        ├── listener.py
        ├── main.py
        └── rag.py
```

## The project structure explicitly separates the backend into domain modules, the frontend into SvelteKit routes and shared libraries, and the RAG service into an independent Python application.

# Directory Responsibilities

## Backend

### `common/`

Contains shared DTOs, exception handling, base models, soft-delete/timestamp abstractions, and cursor-paging utilities.

### `configs/`

Centralizes infrastructure configuration:

* Async execution
* GraphQL
* Kafka
* Redis
* Scheduling
* Security
* Storage

### `modules/auth/`

Responsible for:

* Authentication
* JWT generation/verification
* Refresh token management
* Token rotation
* Security filters

### `modules/knowledge/`

Owns lessons, questions, categories, CRUD operations, and knowledge domain events.

### `modules/feed/`

Maintains feed projections, feed queries, cursor pagination, and Kafka event listeners for synchronizing feed data.

### `modules/interaction/`

Owns:

* Comments
* Replies
* Votes
* Interaction events
* Counter processing

### `modules/chat/`

Handles chat rooms, messages, read state, message pagination, sending, deleting, and unread counts.

### `modules/chatbot/`

Provides the backend integration layer for communicating with the RAG/AI service.

### `modules/media/`

Provides media-upload and cloud-storage integration.

### `modules/user/`

Handles user profiles, follows, roles, and user-related queries.

---

# Frontend Structure

## `src/routes/`

Contains SvelteKit route groups and page-level functionality.

### `(app)/`

Authenticated application experience including:

* Feed
* Knowledge detail
* Knowledge creation/editing
* User profile
* Search

### `(auth)/`

Authentication-related routes such as login and logout.

### `api/`

SvelteKit server endpoints acting as a secure application-side boundary for GraphQL and media upload requests.

### `chat/`

Dedicated community chat page.

### `my-content/`

User-owned content management interface.

---

## `src/lib/`

### `auth/`

Secure session and authentication utilities.

### `components/`

Reusable UI components including:

* Header
* Footer
* Sidebar
* Toast
* Feed item
* Comment item
* Vote controls
* Form components

### `graphql/`

Stores GraphQL operations used by the frontend.

### `generated/`

Contains automatically generated GraphQL TypeScript types and SDK code.

The generated SDK includes queries for feed, search, knowledge detail, and other GraphQL operations.

### `services/`

Contains frontend service abstractions for:

* Authentication
* User management
* Interactions
* Media
* GraphQL communication

### `stores/`

Contains client-side state management such as reference data and UI/application state.

### `types/`

Contains TypeScript domain types.

---

# Design System & UI/UX Highlights

## **Modern Educational SaaS Interface**

The frontend is built with Svelte 5 and Tailwind CSS, favoring lightweight reusable components and responsive layouts.

## **Reusable Component System**

Shared components such as:

* Header
* Sidebar
* Footer
* Toast
* FeedItem
* CommentItem
* Vote controls

are separated from route-specific code to keep UI behavior reusable.

## **Knowledge-Oriented Feed**

The feed visually differentiates:

* Lessons
* Questions

through their type-specific metadata.

A `LessonFeedMeta` can display difficulty, estimated learning time, and Markdown content, while a `QuestionFeedMeta` exposes resolution information and accepted answers.

## **Responsive Layout**

The SvelteKit application uses responsive Tailwind utility classes and browser viewport metadata to support different screen sizes.

## **Interactive Feedback**

The application provides:

* Toast notifications
* Loading states
* Disabled submit states
* Inline validation
* Dynamic vote states
* Dynamic comments and replies

---

# System Architecture

EduShare is composed of three major application layers:

```text
                         ┌──────────────────────┐
                         │     SvelteKit UI      │
                         │ Svelte 5 + TypeScript │
                         └──────────┬───────────┘
                                    │
                               GraphQL / HTTP
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    Spring Boot API   │
                         │ GraphQL + REST/Auth  │
                         └──────────┬───────────┘
                                    │
          ┌─────────────────────────┼─────────────────────────┐
          │                         │                         │
          ▼                         ▼                         ▼
    PostgreSQL                    Redis                    Kafka
   Primary Data               Hot Counters          Domain Events
          │                                                   │
          │                                                   ▼
          │                                           Feed / RAG Consumers
          │                                                   │
          └──────────────────────────────┬────────────────────┘
                                         │
                                         ▼
                              ┌──────────────────────┐
                              │    FastAPI RAG       │
                              │ Chunk / Embed /      │
                              │ Retrieve / Generate  │
                              └──────────┬───────────┘
                                         │
                                         ▼
                                  pgvector / Ollama
```

---

# Event-Driven Architecture

Knowledge changes generate domain events such as:

```text
LESSON_CREATED
LESSON_UPDATED
LESSON_DELETED

QUESTION_CREATED
QUESTION_UPDATED
QUESTION_DELETED
```

The RAG service listens to these events and rebuilds its vector representation when content changes.

The feed module similarly contains Kafka listeners for knowledge and interaction events.

---

# Feed Projection Architecture

The feed is intentionally separated from the original knowledge representation:

```text
Original Knowledge
       │
       │ Domain Event
       ▼
     Kafka
       │
       ▼
 Feed Projection
       │
       ├── FeedItem
       ├── UserFeed
       └── Feed Metadata
```

The `user_feed` database model acts as a fan-out projection for user feeds, while `feed_item` provides a read-oriented representation of knowledge.

---

# Redis Counter Architecture

High-frequency interaction counters are handled independently from the main relational write path.

```text
User Interaction
       │
       ▼
Counter Service
       │
       ▼
Redis
       │
       │ accumulated delta
       ▼
Counter Flush Scheduler
       │
       ▼
PostgreSQL
```

The interaction module contains dedicated components including:

```text
CounterService
CounterServiceImpl
CounterDelta
CounterListener
CounterFlushScheduler
RedisCounterConstants
```

This architecture is intended to reduce contention when many users simultaneously update views, comments, or votes.

---

# API / System Architecture

## Authentication API

Authentication is primarily exposed through Spring endpoints for:

| Operation    | Description                                       |
| ------------ | ------------------------------------------------- |
| Register     | Create user account                               |
| Login        | Authenticate with username/email and password     |
| Refresh      | Rotate refresh token and issue a new access token |
| Logout       | Revoke/terminate refresh-token session            |
| Google Login | OAuth-based authentication                        |

The frontend maps these backend endpoints through a centralized API configuration.

---

# GraphQL API

EduShare uses GraphQL as the main data-query interface.

## Feed Queries

```graphql
getFeed(input: FeedQueryInput)

searchFeed(input: FeedSearchInput!)
```

## Knowledge Queries

```graphql
knowledge(id: ID!)

myKnowledgeList(input: MyKnowledgeFilterInput)

knowledgeListByUsername(
    username: String!
    input: MyKnowledgeFilterInput
)
```

## Knowledge Mutations

```graphql
createLesson(...)
createQuestion(...)
updateLesson(...)
updateQuestion(...)
deleteKnowledge(...)
```

## Comment Queries

```graphql
listRootComments(...)
listCommentReplies(...)
```

## Chat Queries & Mutations

```graphql
rooms
messages(...)

sendMessage(...)
markRead(...)
deleteMessage(...)
```

---

# Installation and Setup

## Prerequisites

Recommended development environment:

```text
Java 21+
Node.js 20+
Docker
Docker Compose
Git
```

The frontend project documentation/mock development content explicitly targets Java 21+ and Node.js 20+.

---

## 1. Clone the Repository

```bash
git clone <your-repository-url>
cd ntl-huy-edu-share
```

---

# 2. Start Infrastructure

Navigate to the backend directory:

```bash
cd edushare-backend
```

Start PostgreSQL, Kafka, Kafka UI, and Redis:

```bash
docker compose up -d
```

The provided Compose configuration exposes:

```text
PostgreSQL   → localhost:5432
Kafka        → localhost:9092
Kafka UI     → localhost:8081
Redis        → localhost:6379
```

---

# 3. Configure Backend Environment

Create:

```text
edushare-backend/.env
```

based on:

```text
edushare-backend/.env.example
```

Example:

```env
JWT_SECRET_KEY=your-super-secret-key-min-32-chars-for-hs256
JWT_ACCESS_TTL_SECONDS=900
```

The backend currently reads JWT configuration from environment/application configuration.

Additional configuration is required for:

* PostgreSQL
* Redis
* Kafka
* Cloudinary
* Storage
* Application-specific GraphQL/security settings

---

# 4. Run the Backend

From `edushare-backend/`:

### Windows

```bash
mvnw.cmd spring-boot:run
```

### macOS / Linux

```bash
./mvnw spring-boot:run
```

The repository includes Maven Wrapper scripts, so installing Maven globally is not required.

---

# 5. Database Initialization

The backend uses Flyway migrations stored under:

```text
src/main/resources/db/migration/
```

Current migrations include:

```text
V1__Initial_schema.sql
V2__create_knowledge_tables.sql
V3__create_feed_item_and_user_feed.sql
```

Database seed scripts are also provided for categories, chat, and users.

---

# 6. Start the Frontend

Navigate to:

```bash
cd ../edushare-frontend
```

Install dependencies:

```bash
npm install
```

Run the development server:

```bash
npm run dev
```

The frontend's package scripts define `vite dev` as the development command.

---

# 7. Configure Frontend Environment

Configure the public API endpoints required by the SvelteKit application.

The frontend reads:

```text
PUBLIC_API_BASE_URL
PUBLIC_GRAPHQL_URL
```

through SvelteKit's public environment configuration.

For example:

```env
PUBLIC_API_BASE_URL=http://localhost:8080
PUBLIC_GRAPHQL_URL=http://localhost:8080/graphql
```

Use the actual backend port configured by `application.yaml`.

---

# 8. Generate GraphQL SDK

The frontend uses GraphQL Code Generator to generate TypeScript types and SDK functions.

Run:

```bash
npm run codegen
```

For development:

```bash
npm run codegen:watch
```

The generated SDK is stored under:

```text
src/lib/generated/
```

---

# 9. Run the RAG Service

Navigate to:

```bash
cd ../RAG
```

Install Python dependencies:

```bash
pip install -r requirements.txt
```

The RAG service requires FastAPI, Uvicorn, Sentence Transformers, PyTorch, aiokafka, asyncpg, pgvector, and LangChain text splitters.

---

# 10. Start Ollama

From the `RAG/` directory:

```bash
docker compose up -d
```

The provided Compose configuration starts Ollama at:

```text
http://localhost:11434
```

The default LLM configuration is:

```text
qwen2.5:3b
```

---

# 11. Download the Embedding Model

The project provides:

```text
RAG/app/download_model.py
```

Run:

```bash
python app/download_model.py
```

This downloads:

```text
bkai-foundation-models/vietnamese-bi-encoder
```

and saves it under:

```text
pretrained_models/vietnamese-bi-encoder
```

---

# 12. Run the RAG API

Start FastAPI with Uvicorn according to the entrypoint exposed by:

```text
RAG/app/main.py
```

A typical development command is:

```bash
uvicorn app.main:app --reload
```

The exact exported application object should be verified against the current `main.py` implementation before deployment.

---

# Development Commands

## Frontend

```bash
npm run dev
npm run build
npm run preview
npm run check
npm run lint
npm run format
npm run codegen
npm run codegen:watch
```

## Backend

```bash
./mvnw spring-boot:run
./mvnw clean package
./mvnw test
```

## Infrastructure

```bash
docker compose up -d
docker compose down
docker compose logs -f
```

---

# Completed Features & Future Enhancements

## Completed Features

### User & Authentication

* [x] User registration
* [x] Username/email authentication
* [x] JWT access-token authentication
* [x] Refresh-token flow
* [x] Refresh-token rotation
* [x] Logout
* [x] Google OAuth integration
* [x] Role-aware authentication
* [x] Profile management
* [x] Follow relationships

### Knowledge

* [x] Lesson creation
* [x] Question creation
* [x] Lesson editing
* [x] Question editing
* [x] Knowledge deletion
* [x] Categories
* [x] Public/private knowledge
* [x] Comment permission configuration
* [x] Lesson difficulty
* [x] Estimated learning time
* [x] Question resolution state
* [x] Accepted answer support

### Feed

* [x] Personalized feed
* [x] Feed projections
* [x] Cursor-based pagination
* [x] Feed search
* [x] Category filtering
* [x] Knowledge-type filtering
* [x] Lesson-level filtering
* [x] Feed metadata projections

### Interaction

* [x] Upvote
* [x] Downvote
* [x] Vote state
* [x] View counters
* [x] Comment counters
* [x] Nested comments
* [x] Comment replies
* [x] Comment pagination
* [x] Soft-delete metadata for comments

### Chat

* [x] Community chat rooms
* [x] Message persistence
* [x] Message replies
* [x] Message deletion
* [x] Read-state tracking
* [x] Unread count calculation
* [x] Cursor-based message pagination

### AI / RAG

* [x] Dedicated FastAPI RAG service
* [x] Vietnamese embedding model
* [x] Lesson chunking
* [x] Question chunking
* [x] Kafka-driven knowledge synchronization
* [x] Vector storage with pgvector
* [x] HNSW vector index
* [x] Semantic similarity retrieval
* [x] Similarity threshold filtering
* [x] Visibility-aware retrieval
* [x] Ollama integration
* [x] Source information in chatbot responses

### Infrastructure

* [x] PostgreSQL
* [x] pgvector
* [x] Kafka
* [x] Kafka UI
* [x] Redis
* [x] Docker Compose infrastructure
* [x] Flyway database migration
* [x] Cloudinary integration
* [x] GraphQL API
* [x] Generated TypeScript GraphQL SDK

---

# Future Enhancements

## Real-Time Communication

* [ ] Add WebSocket / GraphQL Subscription transport for chat
* [ ] Push new messages to connected clients
* [ ] Real-time unread notification updates
* [ ] Real-time feed interaction synchronization

## Feed & Scalability

* [ ] Fully productionize hybrid fan-out strategy
* [ ] Add ranking/recommendation signals
* [ ] Add trending/discovery fallback
* [ ] Optimize feed projection recovery/replay
* [ ] Add load testing for high-follower users

## Interaction Performance

* [ ] Complete Redis counter buffering and flush monitoring
* [ ] Add counter reconciliation jobs
* [ ] Add metrics for Redis/PostgreSQL divergence
* [ ] Add high-concurrency load testing

## AI / RAG

* [ ] Improve retrieval ranking
* [ ] Add reranking models
* [ ] Add streaming chatbot responses
* [ ] Add conversation memory
* [ ] Add RAG evaluation datasets
* [ ] Add retrieval-quality metrics
* [ ] Improve prompt management
* [ ] Add document ingestion monitoring
* [ ] Add automated embedding regeneration workflows

## Security

* [ ] Strengthen refresh-token concurrency handling
* [ ] Add token-family/reuse detection
* [ ] Add security audit logging
* [ ] Add rate limiting
* [ ] Add fine-grained authorization policies
* [ ] Improve secrets management for deployment environments

## Frontend

* [ ] Complete remaining production API integrations
* [ ] Replace development mock data with fully API-driven state where applicable
* [ ] Improve optimistic interaction handling
* [ ] Add offline/error recovery UX
* [ ] Improve accessibility
* [ ] Add richer loading skeletons

The current frontend contains development-oriented mock data for profiles, knowledge, comments, votes, and followers, so these views should be reviewed before treating every screen as fully API-backed.

## DevOps & Observability

* [ ] Containerize the complete stack
* [ ] Add CI/CD pipeline
* [ ] Add application metrics
* [ ] Add distributed tracing
* [ ] Add structured logging
* [ ] Add health checks
* [ ] Add centralized log aggregation
* [ ] Add production monitoring dashboards

---

# Architecture Principles

EduShare is designed around several important software-engineering principles.

## **Modular Domain Boundaries**

Business domains are separated into independent packages rather than placing all entities and services into a single shared layer.

```text
Auth
Knowledge
Feed
Interaction
Chat
Chatbot
Media
User
```

This structure keeps domain responsibilities explicit and makes future extraction into independent services more feasible.

## **Event-Driven Processing**

Kafka is used to move knowledge and interaction events asynchronously between modules and supporting services.

## **Read-Optimized Projections**

Feed and RAG data are represented through specialized read models instead of repeatedly querying complex source-domain structures.

## **Cache-Assisted High-Frequency Operations**

Redis is used for operations that can become write hotspots, particularly interaction counters.

## **Secure Token Lifecycle**

The authentication architecture separates short-lived access tokens from refresh tokens and performs refresh-token rotation.

## **Vector-Aware Knowledge Retrieval**

The RAG subsystem keeps semantic embeddings alongside visibility metadata, allowing retrieval to remain both relevant and access-aware.

---

# Project Status

EduShare is a technically ambitious educational social platform combining conventional application development with several advanced backend concepts.

The project currently demonstrates:

```text
Modern Frontend
       +
GraphQL API
       +
Modular Spring Boot Backend
       +
PostgreSQL
       +
Kafka Event Processing
       +
Redis Caching
       +
Cloudinary Media Storage
       +
Vietnamese Semantic Search
       +
RAG / LLM Integration
```

Rather than being limited to a traditional CRUD application, EduShare explores how a knowledge-sharing platform can be designed around **modular domain boundaries, event-driven processing, read projections, cursor pagination, high-frequency counter optimization, secure token lifecycle management, and vector-based AI retrieval**.

---
