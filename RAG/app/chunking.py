import re
from typing import List


from langchain_text_splitters import (
    MarkdownHeaderTextSplitter,
    RecursiveCharacterTextSplitter,
)

# Cấu hình kích thước Chunk
# ~1000 ký tự tương đương khoảng 200 - 250 từ tiếng Việt
CHUNK_SIZE = 1000
CHUNK_OVERLAP = 150  # Gối đầu ~30-40 từ để không đứt ngữ cảnh

# Bộ cắt theo ký tự (Dùng chung cho cả Lesson dài và Question)
recursive_splitter = RecursiveCharacterTextSplitter(
    chunk_size=CHUNK_SIZE,
    chunk_overlap=CHUNK_OVERLAP,
    separators=["\n\n", "\n", " ", ""]  # Ưu tiên tách theo đoạn văn trước
)


def chunk_lesson(content_markdown: str) -> List[str]:
    """LESSON:
    1. Tách theo các cấp Heading (#, ##, ###) để giữ cấu trúc bài học.
    2. Section nào còn dài quá CHUNK_SIZE thì cắt tiếp đệ quy có overlap.
    """
    if not content_markdown or not content_markdown.strip():
        return []

    # 1. Khai báo các cấp Heading muốn bắt
    headers_to_split_on = [
        ("#", "Header 1"),
        ("##", "Header 2"),
        ("###", "Header 3"),
    ]
    markdown_splitter = MarkdownHeaderTextSplitter(
        headers_to_split_on=headers_to_split_on,
        strip_headers=False  # Giữ lại dòng Heading (# ...) trong nội dung chunk
    )

    # Cắt thành các Document dựa theo Header
    header_docs = markdown_splitter.split_text(content_markdown)

    # 2. Cắt tiếp các section bị quá dài bằng RecursiveSplitter
    final_docs = recursive_splitter.split_documents(header_docs)

    # Trả về danh sách chuỗi văn bản (List[str])
    return [doc.page_content for doc in final_docs]


def chunk_question(title: str, abstract: str, content: str) -> List[str]:
    """QUESTION:
    Nối Title + Abstract + Content thành 1 văn bản.
    Thường câu hỏi sẽ ngắn, nếu dài quá CHUNK_SIZE thì cắt bằng RecursiveSplitter.
    """
    parts = [p.strip() for p in [title, abstract, content] if p and p.strip()]
    full_text = "\n\n".join(parts)

    if not full_text:
        return []

    # Trực tiếp cắt đoạn text đầy đủ
    return recursive_splitter.split_text(full_text)
