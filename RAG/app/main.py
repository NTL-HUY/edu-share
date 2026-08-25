import os
from contextlib import asynccontextmanager
import requests
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException

from app import db
from app.dto import EmbedResponse, EmbedRequest, ChatResponse, ChatRequest, ChatSource
from app.embedding import embed_texts
from app.listener import start_consumer_task, stop_consumer_task
from app.rag import retrieve_context

load_dotenv()
OLLAMA_URL = os.getenv("OLLAMA_URL", "http://localhost:11434/api/generate")
OLLAMA_MODEL = os.getenv("OLLAMA_MODEL", "qwen2.5:3b")

@asynccontextmanager
async def lifespan(app: FastAPI):
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

    embeddings = embed_texts(request.texts, normalize=request.normalize)
    return EmbedResponse(
        model=os.getenv("MODEL_NAME", "bkai-foundation-models/vietnamese-bi-encoder"),
        dim=len(embeddings[0]),
        embeddings=embeddings,
    )

@app.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    query = request.query.strip()
    if not query:
        raise HTTPException(status_code=400, detail="query không được rỗng")

    context_text, sources = await retrieve_context(
        query=query,
        user_id=request.userId,
        visible_owner_ids=request.visibleOwnerIds,
    )

    if context_text:
        prompt = f"""Dựa vào thông tin ngữ cảnh bên dưới để trả lời câu hỏi bằng tiếng Việt một cách ngắn gọn, chính xác.
            Nếu ngữ cảnh không đủ để trả lời, hãy nói rõ là không tìm thấy thông tin liên quan, không được bịa ra câu trả lời.
            ---------------------
            {context_text}
            ---------------------
            Câu hỏi: {query}
            Trả lời:"""
    else:
        prompt = (
            f"Câu hỏi sau không có dữ liệu liên quan trong hệ thống. "
            f"Hãy trả lời bằng tiếng Việt rằng bạn chưa có đủ thông tin để trả lời chính xác, "
            f"đừng tự bịa nội dung.\nCâu hỏi: {query}"
        )
    try:
        payload = {
            "model": request.model or OLLAMA_MODEL,
            "prompt": prompt,
            "stream": request.stream,
        }
        response = requests.post(OLLAMA_URL, json=payload, timeout=60)

        if response.status_code != 200:
            raise HTTPException(
                status_code=500, detail=f"Lỗi từ Ollama API: {response.text}"
            )

        data = response.json()
        answer = data.get("response", "").strip()

        return ChatResponse(
            model=OLLAMA_MODEL,
            answer=answer,
            sources=[ChatSource(**s) for s in sources],
            prompt=prompt
        )

    except requests.exceptions.ConnectionError:
        raise HTTPException(
            status_code=503,
            detail="Không thể kết nối tới Ollama Docker! Hãy kiểm tra xem container edushare-ollama đã 'up' chưa.",
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)