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

    # --- Ollama & Embedding ---
    OLLAMA_URL: str = os.getenv("OLLAMA_URL", "http://localhost:11434/api/generate")
    OLLAMA_MODEL: str = os.getenv("OLLAMA_MODEL", "qwen2.5:3b")
    MODEL_NAME: str = os.getenv("MODEL_NAME", "./pretrained_models/vietnamese-bi-encoder")

    # --- Kafka ---
    KAFKA_BOOTSTRAP: str = os.getenv("KAFKA_BOOTSTRAP", "localhost:9092")

    # --- RAG Parame ---
    CHUNK_SIZE: int = int(os.getenv("CHUNK_SIZE", "1000"))
    CHUNK_OVERLAP: int = int(os.getenv("CHUNK_OVERLAP", "150"))
    RAG_TOP_K: int = int(os.getenv("RAG_TOP_K", "6"))
    RAG_MIN_SIMILARITY: float = float(os.getenv("RAG_MIN_SIMILARITY", "0.35"))

config = Config()