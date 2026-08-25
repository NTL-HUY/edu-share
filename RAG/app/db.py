import os
from typing import List, Optional, Sequence

import asyncpg
from pgvector.asyncpg import register_vector

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = os.getenv("DB_PORT", "5432")
DB_NAME = os.getenv("DB_NAME", "edushare")
DB_USERNAME = os.getenv("DB_USERNAME", "postgres")
DB_PASSWORD = os.getenv("DB_PASSWORD", "postgres")

# Ghép thành chuỗi DSN chuẩn cho asyncpg
DB_DSN = f"postgresql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

pool: Optional[asyncpg.Pool] = None

async def init_pool():
    global pool
    if pool is not None:
        return pool

    async def init_connection(conn: asyncpg.Connection):
        # Đăng ký codec để asyncpg hiểu kiểu vector <-> list[float] đăng ký register_vector để hiểu cách đổi mảng
        # list[float] trong Python thành vector trong Postgres
        await register_vector(conn)

    pool = await asyncpg.create_pool(
        dsn=DB_DSN,
        min_size=1,
        max_size=10,
        init=init_connection,
    )
    return pool



async def close_pool():
    global pool
    if pool is not None:
        await pool.close()
        pool = None


def get_pool() -> asyncpg.Pool:
    if pool is None:
        raise RuntimeError("DB pool chưa được khởi tạo, gọi init_pool() trước")
    return pool


async def delete_chunks_by_knowledge_id(knowledge_id: int):
    pool = get_pool()
    await pool.execute(
        "DELETE FROM knowledge_chunk WHERE knowledge_id = $1", knowledge_id
    )


async def insert_chunks(
    knowledge_id: int,
    knowledge_type: str,
    owner_id: int,
    is_public: bool,
    title: str,
    chunks: Sequence[str],
    embeddings: Sequence[Sequence[float]],
):
    assert len(chunks) == len(embeddings), "Số chunk và số embedding phải khớp"

    pool = get_pool()
    async with pool.acquire() as conn:
        async with conn.transaction():
            # 1. BƯỚC BẮT BUỘC: Xóa SẠCH TOÀN BỘ chunk cũ của bài này trước
            await conn.execute(
                "DELETE FROM knowledge_chunk WHERE knowledge_id = $1",
                knowledge_id
            )

            # 2. Chuẩn bị dữ liệu mới
            rows = [
                (
                    knowledge_id,
                    knowledge_type,
                    owner_id,
                    is_public,
                    title,
                    idx,
                    chunk_text,
                    list(embedding),
                )
                for idx, (chunk_text, embedding) in enumerate(zip(chunks, embeddings))
            ]

            await conn.executemany(
                """
                INSERT INTO knowledge_chunk
                    (knowledge_id, knowledge_type, owner_id, is_public, title, chunk_index, content, embedding)
                VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
                """,
                rows,
            )

async def search_similar_chunks(
    query_embedding: Sequence[float],
    top_k: int,
    user_id: Optional[int] = None,
    visible_owner_ids: Optional[List[int]] = None,
):
    pool = get_pool()
    owner_ids = list(visible_owner_ids or [])
    if user_id is not None:
        owner_ids.append(user_id)

    rows = await pool.fetch(
        """
        SELECT knowledge_id, knowledge_type, title, content, chunk_index,
               1 - (embedding <=> $1) AS similarity
        FROM knowledge_chunk
        WHERE is_public = TRUE
           OR owner_id = ANY($2::bigint[])
        ORDER BY embedding <=> $1
        LIMIT $3
        """,
        list(query_embedding),
        owner_ids,
        top_k,
    )
    return [dict(r) for r in rows]