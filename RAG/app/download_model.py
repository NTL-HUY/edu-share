from sentence_transformers import SentenceTransformer

# Tải và lưu về thư mục local
model = SentenceTransformer("bkai-foundation-models/vietnamese-bi-encoder")
model.save("./pretrained_models/vietnamese-bi-encoder")