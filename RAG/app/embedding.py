import httpx
from langchain_core.embeddings import Embeddings
from app.config import config


class DirectJinaEmbeddings(Embeddings):
    def __init__(self, api_key: str, model_name: str = "jina-embeddings-v3", dimensions: int = 768):
        self.api_key = api_key
        self.model_name = model_name
        self.dimensions = dimensions
        self.url = "https://api.jina.ai/v1/embeddings"
        self.headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

    def embed_documents(self, texts: list[str]) -> list[list[float]]:
        payload = {
            "model": self.model_name,
            "dimensions": self.dimensions,
            "task": "retrieval.passage",
            "input": texts
        }
        with httpx.Client(timeout=30.0) as client:
            res = client.post(self.url, json=payload, headers=self.headers)
            res.raise_for_status()
            return [item["embedding"] for item in res.json()["data"]]

    def embed_query(self, text: str) -> list[float]:
        return self.embed_documents([text])[0]

    async def aembed_documents(self, texts: list[str]) -> list[list[float]]:
        payload = {
            "model": self.model_name,
            "dimensions": self.dimensions,
            "task": "retrieval.passage",
            "input": texts
        }
        async with httpx.AsyncClient(timeout=30.0) as client:
            res = await client.post(self.url, json=payload, headers=self.headers)
            res.raise_for_status()
            return [item["embedding"] for item in res.json()["data"]]

    async def aembed_query(self, text: str) -> list[float]:
        res = await self.aembed_documents([text])
        return res[0]


_embed_service: Embeddings | None = None

def get_embed_service() -> Embeddings:
    global _embed_service
    if _embed_service is None:
        if config.EMBEDDING_PROVIDER == "jina":
            print(f"[Embedding] Dùng Jina Cloud API ({config.JINA_MODEL})")
            _embed_service = DirectJinaEmbeddings(
                api_key=config.JINA_API_KEY,
                model_name=config.JINA_MODEL,
                dimensions=768
            )
        else:
            from langchain_huggingface import HuggingFaceEmbeddings
            print(f" [Embedding] Dùng Local Model ({config.LOCAL_MODEL})")
            _embed_service = HuggingFaceEmbeddings(
                model_name=config.LOCAL_MODEL,
                encode_kwargs={"normalize_embeddings": True}
            )
    return _embed_service


def embed_texts(texts: list[str]) -> list[list[float]]:
    if not texts:
        return []
    return get_embed_service().embed_documents(texts)


async def aembed_texts(texts: list[str]) -> list[list[float]]:
    if not texts:
        return []
    return await get_embed_service().aembed_documents(texts)


def embed_one(text: str) -> list[float]:
    return get_embed_service().embed_query(text)


async def aembed_one(text: str) -> list[float]:
    return await get_embed_service().aembed_query(text)