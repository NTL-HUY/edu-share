from functools import lru_cache
from langchain_core.language_models import BaseChatModel
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_ollama import ChatOllama

from app.config import config

_PROVIDERS = {
    "gemini": lambda mid, t: ChatGoogleGenerativeAI(model=mid, google_api_key=config.GEMINI_API_KEY, temperature=t),
    "ollama": lambda mid, t: ChatOllama(model=mid, base_url=config.OLLAMA_URL, temperature=t),
}

KNOWN_PROVIDERS = {"gemini", "ollama", "openai"}

@lru_cache(maxsize=16)
def get_llm(model=None, temperature=0.2):
    provider, model_id = parse_model(model or config.DEFAULT_MODEL)
    factory = _PROVIDERS.get(provider)
    if not factory:
        raise ValueError(f"Provider không được hỗ trợ: '{provider}'")
    return factory(model_id, temperature)


def parse_model(name: str) -> tuple[str, str]:
    head, sep, tail = name.partition(":")
    if sep and head in KNOWN_PROVIDERS:
        return head, tail
    return "gemini", name