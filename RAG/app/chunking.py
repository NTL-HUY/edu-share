from typing import List, Optional

from langchain_text_splitters import (
    MarkdownHeaderTextSplitter,
    RecursiveCharacterTextSplitter,
)

CHUNK_SIZE = 1000
CHUNK_OVERLAP = 150

recursive_splitter = RecursiveCharacterTextSplitter(
    chunk_size=CHUNK_SIZE,
    chunk_overlap=CHUNK_OVERLAP,
    separators=["\n\n", "\n", " ", ""]
)


def chunk_lesson(content_markdown: str) -> List[str]:

    if not content_markdown or not content_markdown.strip():
        return []

    headers_to_split_on = [
        ("#", "Header 1"),
        ("##", "Header 2"),
        ("###", "Header 3"),
    ]
    markdown_splitter = MarkdownHeaderTextSplitter(
        headers_to_split_on=headers_to_split_on,
        strip_headers=False
    )

    header_docs = markdown_splitter.split_text(content_markdown)

    final_docs = recursive_splitter.split_documents(header_docs)

    return [doc.page_content for doc in final_docs]


def chunk_question(
    title: str,
    abstract: str,
    content: str,
    accepted_answer: Optional[str] = None,
) -> List[str]:
    parts = [p.strip() for p in [title, abstract, content] if p and p.strip()]
    if accepted_answer and accepted_answer.strip():
        parts.append("Câu trả lời được chấp nhận:\n" + accepted_answer.strip())
    full_text = "\n\n".join(parts)

    if not full_text:
        return []

    return recursive_splitter.split_text(full_text)
