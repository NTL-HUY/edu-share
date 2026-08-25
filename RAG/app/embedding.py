import os
from sentence_transformers import SentenceTransformer

MODEL_NAME = os.getenv("MODEL_NAME", "bkai-foundation-models/vietnamese-bi-encoder")

embed_model = None

def get_embed_model() -> SentenceTransformer:
    global embed_model
    if embed_model is None:
        print(f"Loading embedding model: {MODEL_NAME}...")
        embed_model = SentenceTransformer(MODEL_NAME)
        print("Model loaded successfully!")
    return embed_model


def embed_texts(texts: list[str], normalize: bool = True):
    model = get_embed_model()
    return model.encode(
        texts, normalize_embeddings=normalize, convert_to_numpy=True
    ).tolist()

def embed_one(text: str, normalize: bool = True):
    return embed_texts([text], normalize=normalize)[0]