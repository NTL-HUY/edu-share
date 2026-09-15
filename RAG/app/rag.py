from typing import List, Optional

from app import db
from app.chunking import chunk_lesson, chunk_question
from app.config import config
from app.dto import (
    KnowledgeCreatedEvent,
    KnowledgeDeletedEvent,
    KnowledgeUpdatedEvent,
    LessonCreatedEvent,
    QuestionCreatedEvent,
    LessonUpdatedEvent,
    QuestionUpdatedEvent,
)
from app.embedding import aembed_texts, aembed_one


async def handle_knowledge_create_event(event: KnowledgeCreatedEvent):
    if isinstance(event, LessonCreatedEvent):
        chunks = chunk_lesson(event.contentMarkdown)
    elif isinstance(event, QuestionCreatedEvent):
        return
    else:
        print(f"[rag] Không hỗ trợ loại event: {type(event).__name__}")
        return

    if not chunks:
        print(f"[rag] knowledge_id={event.knowledgeId} không có nội dung để chunk, dừng")
        return

    embeddings = await aembed_texts(chunks)
    await db.insert_chunks(
        knowledge_id=event.knowledgeId,
        knowledge_type=event.type.value,
        owner_id=event.ownerId,
        is_public=event.isPublic,
        title=event.title,
        chunks=chunks,
        embeddings=embeddings,
    )
    print(f"[rag] Đã lưu {len(chunks)} chunk cho knowledge_id={event.knowledgeId}")

async def handle_knowledge_update_event(event: KnowledgeUpdatedEvent):
    if isinstance(event, LessonUpdatedEvent):
        chunks = chunk_lesson(event.contentMarkdown)
    elif isinstance(event, QuestionUpdatedEvent):
        if not event.isResolved:
            print(f"[rag] Question knowledge_id={event.knowledgeId} chưa resolved, bỏ qua")
            return
        accepted_answer = await db.get_accepted_comment_content(
            event.knowledgeId, event.acceptedAnswerId
        )
        chunks = chunk_question(
            title=event.title,
            abstract=event.abstractText or "",
            content=event.content,
            accepted_answer=accepted_answer,
        )
        print("chunkkkk",chunks)
    else:
        print(f"[rag] Không hỗ trợ loại event: {type(event).__name__}")
        return

    if not chunks:
        print(f"[rag] knowledge_id={event.knowledgeId} không có nội dung để chunk, dừng")
        return

    embeddings = await aembed_texts(chunks)
    await db.insert_chunks(
        knowledge_id=event.knowledgeId,
        knowledge_type=event.type.value,
        owner_id=event.ownerId,
        is_public=event.isPublic,
        title=event.title,
        chunks=chunks,
        embeddings=embeddings,
    )
    print(f"[rag] Đã lưu {len(chunks)} chunk cho knowledge_id={event.knowledgeId}")


async def handle_knowledge_delete_event(event: KnowledgeDeletedEvent):
    await db.delete_chunks_by_knowledge_id(event.knowledgeId)
    print(f"[rag] Đã xoá hẳn chunk của knowledge_id={event.knowledgeId}")


async def retrieve_context(
    query: str,
    user_id: Optional[int] = None,
    visible_owner_ids: Optional[List[int]] = None,
    top_k: int = config.RAG_TOP_K,
    min_similarity: float = config.RAG_MIN_SIMILARITY,
):
    query_embedding = await aembed_one(query)
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