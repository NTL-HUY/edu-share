# Thiết Kế Xử Lý UI Tạo Bài Đăng & Xem Chi Tiết Bài Đăng

## 1. Giao diện Tạo bài đăng & Xem chi tiết bài đăng (`/feed/create` & `/feed/[id]`)

| STT | Tên xử lý | Điều kiện | Ý nghĩa |
|---|---|---|---|
| 1 | `load_reference_data` | Khi giao diện tạo bài đăng được nạp lên | Nạp danh sách danh mục môn học (`categories`) và mức độ (`lessonLevels`) từ store dùng chung để đổ vào các dropdown lựa chọn. |
| 2 | `select_post_type` | Khi người dùng click tab "Bài học (Lesson)" hoặc "Hỏi đáp (Question)" | Cập nhật trạng thái `postType` trong form state, kích hoạt hiển thị đúng vùng nội dung chuyên biệt: bộ `LessonFormFields` (markdown...) hoặc `QuestionFormFields` (khối textarea câu hỏi). |
| 3 | `input_title` | Khi người dùng nhập tiêu đề vào ô "Tiêu đề *" | Ghi nhận giá trị `title`, bắt buộc nhập, placeholder thay đổi theo loại bài đăng hiện tại (ví dụ gợi ý chủ đề GraphQL). |
| 4 | `select_category` | Khi người dùng thay đổi lựa chọn tại Dropdown "Danh mục" | Ghi nhận `categoryId` của danh mục môn học cho bài đăng. |
| 5 | `set_thumbnail` | Khi người dùng nhập URL vào ô "Ảnh Thumbnail" hoặc chọn file tải lên | Nếu nhập URL: lưu trực tiếp `thumbnailUrl`. Nếu chọn file: gọi `/api/media/upload` (POST multipart, folder `LESSON_THUMBNAIL`) rồi cập nhật `thumbnailUrl`, hiển thị trạng thái "Đang tải..." khi upload. |
| 6 | `toggle_isPublic` | Khi người dùng bỏ tích checkbox "Công khai" | Đổi cờ `isPublic`, quyết định bài đăng hiển thị công khai hay riêng tư. |
| 7 | `toggle_allowComment` | Khi người dùng bỏ tích checkbox "Cho phép bình luận" | Đổi cờ `allowComment`, quyết định khu vực bình luận ở trang chi tiết có mở hay không. |
| 8 | `input_abstract` | Khi người dùng nhập ô "Tóm tắt ngắn" | Ghi nhận `abstractText` – mô tả 1-2 câu hiển thị trên bảng tin. |
| 9 | `input_lesson_fields` | Khi loại bài đăng là "Bài học" và người dùng nhập các trường Mức độ / Thời gian đọc / Nội dung Markdown | Ghi nhận `level`, `estimateTimeInMinutes` (min 1), `contentMarkdown` qua trình soạn thảo Markdown (carta-md có toolbar + HTML renderer). |
| 10 | `input_question_content` | Khi loại bài đăng là "Hỏi đáp" và người dùng nhập "Chi tiết câu hỏi *" | Ghi nhận `questionContent` – mô tả vấn đề, bắt buộc nhập, dạng textarea thuần. |
| 11 | `submit_post` | Khi người dùng nhấn nút gửi (Đăng Bài Học / Đăng Câu Hỏi / Lưu thay đổi) | Nếu chưa có `editingId`: gọi GraphQL `CreateLesson`/`CreateQuestion`; nếu đang chỉnh sửa: gọi `UpdateLesson`/`UpdateQuestion`. Cập nhật cờ `isSubmitting` để vô hiệu hóa nút, hiển thị toast thành công/lỗi theo thông báo từ server. |
| 12 | `load_post_detail` | Khi trang `/feed/[id]` được nạp | Loader server gọi GraphQL `GetKnowledgeDetail` (kèm bình luận, cursor=null, limit=2) để lấy dữ liệu bài đăng; nếu không tồn tại trả lỗi "Bài viết không tồn tại hoặc đã bị xóa". |
| 13 | `render_post_badges` | Sau khi dữ liệu bài đăng được nạp | Hiển thị badge xác định loại nội dung: `LESSON` (kèm badge mức độ) hoặc `QUESTION` (kèm trạng thái "Đã có lời giải" / "Chưa có lời giải") cùng tên danh mục môn học. |
| 14 | `render_title_metadata` | Sau khi dữ liệu bài đăng được nạp | Hiển thị tiêu đề bài đăng, avatar + tên tác giả, thời gian đăng (định dạng tương đối), số lượt xem, và thời gian đọc (chỉ với bài học). |
| 15 | `render_content_body` | Sau khi dữ liệu bài đăng được nạp | Với bài học: render nội dung Markdown qua `MarkdownContent`; với hỏi đáp: hiển thị `content` dạng văn bản giữ nguyên định dạng. |
| 16 | `vote_post` | Khi người dùng click nút Upvote/Downvote ở cột điều hướng | Cập nhật tối ưu (optimistic) điểm số và trạng thái vote hiển thị ngay, rồi gọi `/feed/{id}/vote` (POST); nếu thất bại khôi phục lại giá trị cũ và báo lỗi. Click lần nữa cùng giá trị sẽ hủy vote. |
| 17 | `load_initial_comments` | Sau khi dữ liệu bài đăng được nạp | Khởi tạo state bình luận với danh sách ban đầu, cursor và cờ `hasMore` từ dữ liệu server để sẵn sàng phân trang. |
| 18 | `send_comment` | Khi người dùng nhấn "Gửi bình luận" trong khung soạn thảo | Kiểm tra nội dung không rỗng, gọi PUT `/feed/{id}/comment` (kèm `replyToCommentId` nếu đang trả lời), prepend bình luận mới vào danh sách (hoặc vào replies khi đang trả lời), xóa nội dung và báo toast thành công. |
| 19 | `reply_comment` | Khi người dùng click "Trả lời" trên một bình luận | Đánh dấu bình luận đang được trả lời (hiện khung cảnh báo "Đang trả lời @..."); khi mở lần đầu gọi `listCommentReplies` để tải các câu trả lời, lần sau chỉ đóng/mở. |
| 20 | `load_more_comments` | Khi người dùng click "Xem thêm bình luận" và còn `hasMore` | Gọi `ListRootComments` với `cursor` hiện tại, nối thêm danh sách bình luận đã tải và cập nhật cursor mới, cờ `hasMore`. |
| 21 | `resolved_status` | Khi bài đăng thuộc loại "Hỏi đáp" | Hiển thị trạng thái giải đáp của câu hỏi ("Đã có lời giải" / "Chưa có lời giải") với màu sắc phân biệt. |

## 2. Giao diện Tìm kiếm & Khám phá (`/search`)

| STT | Tên xử lý | Điều kiện | Ý nghĩa |
|---|---|---|---|
| 1 | `load_search_results` | Khi giao diện tìm kiếm được nạp lên hoặc các tham số bộ lọc thay đổi | Gọi hệ thống để lấy danh sách bài viết trùng khớp. |
| 2 | `update_search_keyword` | Khi người dùng nhập từ khóa vào ô "Tìm kiếm..." trên Header và nhấn Enter | Cập nhật tham số `q` lên thanh URL trình duyệt, kích hoạt nạp lại dữ liệu bài đăng theo từ khóa. |
| 3 | `filter_by_type` | Khi người dùng click chọn tab bộ lọc "Bài học" hoặc "Hỏi đáp" | Cập nhật tham số `type` lên URL, chuyển trạng thái tab active để lấy đúng loại học liệu cần tìm. |
| 4 | `filter_by_category` | Khi người dùng thay đổi lựa chọn tại Dropdown danh mục môn học | Ghi nhận ID danh mục môn học, đồng bộ lên URL trình duyệt để lọc bài đăng thuộc môn học tương ứng và đưa số trang `page` về 0. |

*Ghi chú: cả hai luồng tạo và chỉnh sửa bài đăng dùng chung `CreatePostState` + `PostForm` (chế độ edit qua `editingId`). Hệ thống lọc tìm kiếm hoạt động thuần tuý theo tham số URL (`q`, `type`, `category`, `level`, `sort`, `page`) – mọi thay đổi bộ lọc đều `goto` đến URL mới để trigger nạp lại dữ liệu.*

## 3. Kiến trúc hạ tầng dịch vụ

| Dịch vụ | Công nghệ | Cổng | Vai trò |
|---|---|---|---|
| Cơ sở dữ liệu & Vector Store | PostgreSQL 16 + pgvector | 5432 | Lưu trữ dữ liệu quan hệ và vector nhúng 768 chiều |
| Message Broker | Apache Kafka 3.8.0 (kèm Kafka UI) | 9092 (UI: 8081) | Điều phối sự kiện bất đồng bộ giữa các service |
| Bộ đệm In-Memory | Redis 7-alpine | 6379 | Đệm đếm tương tác, cache bảng tin, khóa phân tán |
| Máy chủ suy luận AI cục bộ | Ollama (Qwen2.5:3b) | 11434 | Sinh câu trả lời RAG tiếng Việt |
| Core Backend | Spring Boot 3 (Java 21) | 8080 | API hỗn hợp REST, GraphQL, WebSocket STOMP |
| Vi dịch vụ AI | Python FastAPI | 8000 | Cắt mảnh tài liệu, mã hóa vector, thực thi RAG |
| Giao diện Client | SvelteKit (Svelte 5 Runes + Tailwind CSS v4) | — | Giao tiếp Backend qua SDK TypeScript tự sinh (GraphQL Codegen) |

## 4. Danh mục từ viết tắt

| Từ viết tắt | Từ đầy đủ | Ý nghĩa |
|---|---|---|
| AI | Artificial Intelligence | Trí tuệ nhân tạo |
| RAG | Retrieval-Augmented Generation | Kết hợp truy xuất dữ liệu và mô hình ngôn ngữ lớn |
| KOL | Key Opinion Leader | Tài khoản có lượng người theo dõi lớn |
| HNSW | Hierarchical Navigable Small World | Thuật toán đánh chỉ mục đồ thị cho tìm kiếm vector |
| CQRS | Command Query Responsibility Segregation | Tách biệt luồng ghi và luồng đọc dữ liệu |
| CRUD | Create, Read, Update, Delete | Bốn thao tác cơ bản trên dữ liệu |
| JWT | JSON Web Token | Chuẩn mã hóa token dùng trong xác thực |
| SDK | Software Development Kit | Bộ công cụ phát triển phần mềm |
| API | Application Programming Interface | Giao diện lập trình ứng dụng |
| CSS | Cascading Style Sheets | Ngôn ngữ định dạng giao diện web |
| CNTT | Công nghệ Thông tin | Ngành kỹ thuật xử lý và truyền tải thông tin |
| HTTP | Hypertext Transfer Protocol | Giao thức trao đổi dữ liệu Client-Server |
| REST | Representational State Transfer | Kiến trúc thiết kế API dựa trên HTTP |
| STOMP | Simple Text Orientated Messaging Protocol | Giao thức truyền tin nhắn thời gian thực |
| JSON | JavaScript Object Notation | Định dạng trao đổi dữ liệu dạng văn bản |
| LLM | Large Language Model | Mô hình ngôn ngữ lớn |
| ANN | Approximate Nearest Neighbor | Tìm kiếm vector gần đúng, tốc độ cao |
| IVFPQ | Inverted File with Product Quantization | Kỹ thuật nén và phân cụm chỉ mục vector |
| KD-Tree | k-dimensional Tree | Cây phân chia không gian nhiều chiều |
| SSR | Server-Side Rendering | Dựng HTML tại server trước khi gửi client |
| CSR | Client-Side Rendering | Dựng giao diện tại client bằng JavaScript |
| RDBMS | Relational Database Management System | Hệ quản trị cơ sở dữ liệu quan hệ |
| SPA | Single Page Application | Ứng dụng web một trang, không tải lại |
| SQL | Structured Query Language | Ngôn ngữ truy vấn cơ sở dữ liệu quan hệ |
| TTL | Time To Live | Thời gian tồn tại của dữ liệu/cache |
| NLP | Natural Language Processing | Xử lý ngôn ngữ tự nhiên |
| XML | Extensible Markup Language | Ngôn ngữ đánh dấu lưu trữ dữ liệu có cấu trúc |
| ORM | Object-Relational Mapping | Ánh xạ đối tượng lập trình sang bảng quan hệ |
| 3NF | Third Normal Form | Chuẩn hóa cơ sở dữ liệu mức 3 |
| ACID | Atomicity, Consistency, Isolation, Durability | Bốn tính chất đảm bảo giao dịch cơ sở dữ liệu |
| DTO | Data Transfer Object | Đối tượng trung gian truyền dữ liệu giữa các tầng |
| ACK | Acknowledgement | Tín hiệu xác nhận đã nhận dữ liệu |