import asyncio
import sys
import types
from unittest.mock import AsyncMock, MagicMock

import pytest

sys.modules["app.db"] = types.ModuleType("app.db")
sys.modules["app.embedding"] = types.ModuleType("app.embedding")
sys.modules["app.chunking"] = types.ModuleType("app.chunking")

app_db = sys.modules["app.db"]
app_db.get_accepted_comment_content = AsyncMock(return_value=None)
app_db.insert_chunks = AsyncMock(return_value=None)

app_embedding = sys.modules["app.embedding"]
app_embedding.embed_texts = MagicMock()
app_embedding.embed_one = MagicMock()

app_chunking = sys.modules["app.chunking"]
app_chunking.chunk_lesson = MagicMock()
app_chunking.chunk_question = MagicMock()

from app.dto import LessonUpdatedEvent, QuestionUpdatedEvent
from app.rag import handle_knowledge_update_event


def make_lesson_event():
    return LessonUpdatedEvent.model_validate(
        {
            "eventType": "LESSON_UPDATED",
            "knowledgeId": 1,
            "type": "LESSON",
            "ownerId": 7,
            "title": "Bai hoc Java",
            "abstractText": "Tom tat",
            "thumbnailUrl": "http://img",
            "isPublic": True,
            "allowComment": True,
            "categoryId": 1,
            "categoryName": "Java",
            "updatedAt": "2026-01-01T00:00:00",
            "contentMarkdown": "# Tieu de\nNoi dung bai hoc",
            "level": "BEGINNER",
            "estimateTimeInMinutes": 30,
        }
    )


def make_question_event(resolved=True, accepted_answer_id=99):
    return QuestionUpdatedEvent.model_validate(
        {
            "eventType": "QUESTION_UPDATED",
            "knowledgeId": 2,
            "type": "QUESTION",
            "ownerId": 8,
            "title": "Cau hoi Java",
            "abstractText": "",
            "thumbnailUrl": None,
            "isPublic": True,
            "allowComment": True,
            "categoryId": 1,
            "categoryName": "Java",
            "updatedAt": "2026-01-01T00:00:00",
            "isResolved": resolved,
            "acceptedAnswerId": accepted_answer_id,
            "content": "Lam sao de?",
        }
    )


def test_lesson_updated_chunks_and_inserts(monkeypatch):
    monkeypatch.setattr("app.rag.chunk_lesson", lambda md: ["chunk A", "chunk B"])
    monkeypatch.setattr("app.rag.embed_texts", lambda chunks: [[0.1], [0.2]])
    insert = AsyncMock(return_value=None)
    monkeypatch.setattr(sys.modules["app.db"], "insert_chunks", insert)

    asyncio.run(handle_knowledge_update_event(make_lesson_event()))

    assert insert.await_count == 1
    kwargs = insert.await_args.kwargs
    assert kwargs["knowledge_id"] == 1
    assert kwargs["knowledge_type"] == "LESSON"
    assert kwargs["owner_id"] == 7
    assert kwargs["title"] == "Bai hoc Java"
    assert kwargs["chunks"] == ["chunk A", "chunk B"]
    assert kwargs["embeddings"] == [[0.1], [0.2]]


def test_resolved_question_fetches_and_injects_accepted_answer(monkeypatch):
    get_comment = AsyncMock(return_value="Cau tra loi duoc chap nhan")
    monkeypatch.setattr(sys.modules["app.db"], "get_accepted_comment_content", get_comment)
    chunk_q = MagicMock(return_value=["chunk"])
    monkeypatch.setattr("app.rag.chunk_question", chunk_q)
    monkeypatch.setattr("app.rag.embed_texts", lambda chunks: [[0.9]])
    insert = AsyncMock(return_value=None)
    monkeypatch.setattr(sys.modules["app.db"], "insert_chunks", insert)

    asyncio.run(handle_knowledge_update_event(make_question_event()))

    get_comment.assert_awaited_once_with(2, 99)
    assert chunk_q.call_args.kwargs["accepted_answer"] == "Cau tra loi duoc chap nhan"
    assert chunk_q.call_args.kwargs["title"] == "Cau hoi Java"
    assert insert.await_count == 1
    assert insert.await_args.kwargs["knowledge_type"] == "QUESTION"


def test_unresolved_question_skips_insert(monkeypatch):
    insert = AsyncMock(return_value=None)
    monkeypatch.setattr(sys.modules["app.db"], "insert_chunks", insert)
    get_comment = AsyncMock(return_value="...")
    monkeypatch.setattr(sys.modules["app.db"], "get_accepted_comment_content", get_comment)

    asyncio.run(handle_knowledge_update_event(make_question_event(resolved=False)))

    get_comment.assert_not_awaited()
    insert.assert_not_awaited()


def test_empty_chunks_skips_insert(monkeypatch):
    monkeypatch.setattr("app.rag.chunk_lesson", lambda md: [])
    insert = AsyncMock(return_value=None)
    monkeypatch.setattr(sys.modules["app.db"], "insert_chunks", insert)

    asyncio.run(handle_knowledge_update_event(make_lesson_event()))

    insert.assert_not_awaited()