from datetime import datetime
from typing import List, Optional

from app import db
from app.chunking import chunk_lesson, chunk_question
from app.config import config
from app.embedding import embed_texts, embed_one

# eventType bên Spring publish dạng: LESSON_CREATED, LESSON_UPDATED, LESSON_DELETED,
# QUESTION_CREATED, QUESTION_UPDATED, QUESTION_DELETED
UPSERT_SUFFIXES = ("_CREATED", "_UPDATED")
DELETE_SUFFIXES = ("_DELETED",)


def parse_java_timestamp(arr) -> Optional[datetime]:
    if not arr:
        return None
    y, mo, d, h, mi, s, *rest = arr
    micro = (rest[0] // 1000) if rest else 0
    return datetime(y, mo, d, h, mi, s, micro)


async def handle_knowledge_event(event: dict):
    event_type = event.get("eventType", "")
    knowledge_id = event.get("knowledgeId")

    if knowledge_id is None:
        print(f"[rag] Bỏ qua event thiếu knowledgeId: {event}")
        return

    if event_type.endswith(DELETE_SUFFIXES):
        await db.delete_chunks_by_knowledge_id(knowledge_id)
        print(f"[rag] Đã xoá chunk của knowledge_id={knowledge_id}")
        return

    if event_type.endswith(UPSERT_SUFFIXES):
        await _process_upsert(event)
        return

    print(f"[rag] eventType không nhận diện được, bỏ qua: {event_type}")


async def _process_upsert(event: dict):
    knowledge_id = event["knowledgeId"]
    knowledge_type = event.get("type")
    title = event.get("title", "")
    owner_id = event.get("ownerId")
    is_public = event.get("isPublic", True)

    if knowledge_type == "LESSON":
        chunks = chunk_lesson(event.get("contentMarkdown", ""))
    elif knowledge_type == "QUESTION":
        chunks = chunk_question(
            title=title,
            abstract=event.get("abstractText", ""),
            content=event.get("content", ""),
        )
    else:
        print(f"[rag] type không hỗ trợ: {knowledge_type}")
        return

    if not chunks:
        print(f"[rag] knowledge_id={knowledge_id} không có nội dung để chunk, dừng")
        return

    embeddings = embed_texts(chunks)
    await db.insert_chunks(
        knowledge_id=knowledge_id,
        knowledge_type=knowledge_type,
        owner_id=owner_id,
        is_public=is_public,
        title=title,
        chunks=chunks,
        embeddings=embeddings,
    )
    print(f"[rag] Đã lưu {len(chunks)} chunk cho knowledge_id={knowledge_id}")


async def retrieve_context(
    query: str,
    user_id: Optional[int] = None,
    visible_owner_ids: Optional[List[int]] = None,
    top_k: int = config.RAG_TOP_K,
    min_similarity: float = config.RAG_MIN_SIMILARITY,
):
    query_embedding = embed_one(query)
    rows = await db.search_similar_chunks(
        query_embedding=query_embedding,
        top_k=top_k,
        user_id=user_id,
        visible_owner_ids=visible_owner_ids,
    )

    rows = [r for r in rows if r["similarity"] >= min_similarity]

    if not rows:
        return "", []

    sorted_rows = sorted(rows, key=lambda x: (x["knowledge_id"], x["chunk_index"]))

    context_text = "\n\n---\n\n".join(
        f"[{r['knowledge_type']}] {r['title']} (Phần {r['chunk_index'] + 1})\n{r['content']}"
        for r in sorted_rows
    )

    sources = [
        {
            "knowledgeId": r["knowledge_id"],
            "title": r["title"],
            "type": r["knowledge_type"],
            "similarity": round(float(r["similarity"]), 4),
        }
        for r in rows
    ]

    seen = set()
    unique_sources = []
    for s in sources:
        if s["knowledgeId"] not in seen:
            seen.add(s["knowledgeId"])
            unique_sources.append(s)

    return context_text, unique_sources