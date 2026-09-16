from langchain_core.prompts import ChatPromptTemplate

SYSTEM = """Bạn là trợ lý của hệ thống EduShare. Trả lời bằng tiếng Việt, ngắn gọn, chính xác.
Chỉ dùng thông tin trong phần NGỮ CẢNH. Nếu ngữ cảnh trống hoặc không đủ,
hãy nói rõ là chưa tìm thấy thông tin liên quan trong hệ thống. Tuyệt đối không bịa."""

RAG_PROMPT = ChatPromptTemplate.from_messages([
    ("system", SYSTEM),
    ("human", "NGỮ CẢNH:\n{context}\n\nCâu hỏi: {question}"),
])