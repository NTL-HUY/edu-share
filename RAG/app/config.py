import os
from dotenv import load_dotenv

load_dotenv()


class Config:
    DB_HOST: str = os.getenv("DB_HOST", "localhost")
    DB_PORT: str = os.getenv("DB_PORT", "5432")
    DB_NAME: str = os.getenv("DB_NAME", "edudb")
    DB_USERNAME: str = os.getenv("DB_USERNAME", "admin")
    DB_PASSWORD: str = os.getenv("DB_PASSWORD", "123456")

    @property
    def DB_DSN(self) -> str:
        return f"postgresql://{self.DB_USERNAME}:{self.DB_PASSWORD}@{self.DB_HOST}:{self.DB_PORT}/{self.DB_NAME}"

    # --- Embedding Service ---
    EMBEDDING_PROVIDER: str = os.getenv("EMBEDDING_PROVIDER", "jina").lower()
    JINA_API_KEY: str = os.getenv("JINA_API_KEY", "")
    JINA_MODEL: str = os.getenv("JINA_MODEL", "jina-embeddings-v3")

    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    LOCAL_MODEL: str = os.getenv(
        "LOCAL_MODEL",
        os.path.join(BASE_DIR, "pretrained_models", "vietnamese-bi-encoder"),
    )

    # Property lấy tên model đang hoạt động thực tế
    @ property

    def ACTIVE_MODEL_NAME(self) -> str:
        return self.JINA_MODEL if self.EMBEDDING_PROVIDER == "jina" else self.LOCAL_MODEL

    # --- Ollama ---
    OLLAMA_URL: str = os.getenv("OLLAMA_URL", "http://localhost:11434")
    OLLAMA_MODEL: str = os.getenv("OLLAMA_MODEL", "qwen2.5:3b")
    # MODEL_NAME: str = os.getenv("MODEL_NAME", "./pretrained_models/vietnamese-bi-encoder")

    # --- Gemini ---
    GEMINI_API_KEY: str = os.getenv("GEMINI_API_KEY", "")
    DEFAULT_MODEL: str = os.getenv("DEFAULT_MODEL", "gemini:gemini-3.6-flash")
    # --- Kafka ---
    KAFKA_BOOTSTRAP: str = os.getenv("KAFKA_BOOTSTRAP", "localhost:9092")
    KNOWLEDGE_CREATED_TOPIC: str = os.getenv("KNOWLEDGE_CREATED_TOPIC", "knowledge-created")
    KNOWLEDGE_UPDATED_TOPIC: str = os.getenv("KNOWLEDGE_UPDATED_TOPIC", "knowledge-updated")
    KNOWLEDGE_DELETED_TOPIC: str = os.getenv("KNOWLEDGE_DELETED_TOPIC", "knowledge-deleted")

    # --- RAG Parame ---
    CHUNK_SIZE: int = int(os.getenv("CHUNK_SIZE", "1000"))
    CHUNK_OVERLAP: int = int(os.getenv("CHUNK_OVERLAP", "150"))
    RAG_TOP_K: int = int(os.getenv("RAG_TOP_K", "6"))
    RAG_MIN_SIMILARITY: float = float(os.getenv("RAG_MIN_SIMILARITY", "0.35"))


config = Config()
