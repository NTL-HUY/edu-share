import os
from contextlib import asynccontextmanager
import requests
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from langchain_core.output_parsers import StrOutputParser

from app import db
from app.config import config
from app.dto import EmbedResponse, EmbedRequest, ChatResponse, ChatRequest, ChatSource
from app.embedding import embed_texts
from app.listener import start_consumer_task, stop_consumer_task
from app.llm.llm_factory import get_llm
from app.llm.prompts import RAG_PROMPT
from app.rag import retrieve_context

load_dotenv()
OLLAMA_URL = os.getenv("OLLAMA_URL", "http://localhost:11434/api/generate")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen2.5:3b")

@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Đang kết nối DB...")
    await db.init_pool()
    start_consumer_task()

    yield

    stop_consumer_task()
    await db.close_pool()

app = FastAPI(title="EDU SHARE EMBEDDING SERVICE", lifespan=lifespan)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/embed", response_model=EmbedResponse)
def embed(request: EmbedRequest):
    if not request.texts:
        raise HTTPException(status_code=400, detail="texts không được rỗng")

    embeddings = embed_texts(request.texts)
    return EmbedResponse(
        model=config.ACTIVE_MODEL_NAME,
        dim=len(embeddings[0]),
        embeddings=embeddings,
    )

@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    query = request.query.strip()
    if not query:
        raise HTTPException(400, "query không được rỗng")

    context_text, sources = await retrieve_context(
        query=query,
        user_id=request.userId,
        visible_owner_ids=request.visibleOwnerIds,
    )

    chain = RAG_PROMPT | get_llm(request.model) | StrOutputParser()

    try:
        answer = await chain.ainvoke({
            "context": context_text or "(không có tài liệu liên quan)",
            "question": query,
        })
    except Exception as e:
        raise HTTPException(502, f"Lỗi khi gọi LLM: {e}")

    return ChatResponse(
        model=request.model or config.DEFAULT_MODEL,
        answer=answer.strip(),
        sources=[ChatSource(**s) for s in sources],
        prompt="",
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)