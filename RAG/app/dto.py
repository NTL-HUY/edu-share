from typing import List, Optional

from pydantic import BaseModel


class EmbedRequest(BaseModel):
    texts: List[str]
    normalize: bool = True

class EmbedResponse(BaseModel):
    model: str
    dim: int
    embeddings: List[List[float]]

class ChatRequest(BaseModel):
    query: str
    userId: Optional[int] = None
    visibleOwnerIds: Optional[List[int]] = None
    model: Optional[str] = None
    stream: bool = False

class ChatSource(BaseModel):
    knowledgeId: int
    title: str
    type: str
    similarity: float

class ChatResponse(BaseModel):
    model: str
    answer: str
    sources: List[ChatSource]
    prompt: str
