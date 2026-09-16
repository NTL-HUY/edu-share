-- db/seed_knowledge_feed.sql
-- =====================================================
-- Seed 50 bài: 30 LESSON + 20 QUESTION
-- Kèm feed_item (projection denormalized), user_feed (fan-out cho follower),
-- comment (root + reply) và vote (upvote/downvote).
--
-- CHẠY SAU: seed_user.sql (admin=1, user1..20 = id 2..21) và seed_category.sql (8 category).
-- Idempotent: chạy lại nhiều lần an toàn nhờ NOT EXISTS.
-- =====================================================

DROP TABLE IF EXISTS seed_item;

CREATE TEMP TABLE seed_item (
    n              int PRIMARY KEY,
    type           varchar(10),
    title          varchar(255),
    abstract       varchar(500),
    owner_username varchar(50),
    category_name  varchar(100),
    level          varchar(20),
    estimate_time  int,
    body           text,
    is_resolved    boolean,
    views          int
);

-- =====================================================
-- 1. DỮ LIỆU GỐC: 50 bài
--    LESSON: level + estimate_time + body(content_markdown) được dùng
--    QUESTION: is_resolved + body(content) được dùng
-- =====================================================
INSERT INTO seed_item (n, type, title, abstract, owner_username, category_name, level, estimate_time, body, is_resolved, views) VALUES
(1, 'LESSON', 'Nhập môn Cấu trúc dữ liệu và Giải thuật', 'Tổng quan các cấu trúc dữ liệu cơ bản: mảng, danh sách liên kết, ngăn xếp, hàng đợi và cây, kèm so sánh độ phức tạp từng thao tác.', 'user1', 'Công nghệ Thông tin', 'BEGINNER', 45,
'
# Nhập môn Cấu trúc dữ liệu

Bài viết giới thiệu các cấu trúc dữ liệu cơ bản: mảng, danh sách liên kết, ngăn xếp, hàng đợi và cây.

## Bảng so sánh nhanh
- Mảng: truy cập O(1), chèn xóa O(n)
- Danh sách liên kết: chèn xóa O(1), truy cập O(n)
- Ngăn xếp và hàng đợi: vào ra theo thứ tự đặc trưng (LIFO / FIFO)

Hẹn gặp các bạn ở bài tiếp theo về cây nhị phân tìm kiếm.', NULL, 37),
(2, 'LESSON', 'Mạng máy tính cơ bản: từ LAN đến Internet', 'Giải thích mô hình mạng, địa chỉ IP, DNS và cách gói tin di chuyển từ máy này sang máy khác trong mạng LAN và Internet.', 'user3', 'Công nghệ Thông tin', 'BEGINNER', 60,
'
# Mạng máy tính cơ bản

Một máy tính muốn nói chuyện với máy khác cần địa chỉ định danh. Chúng ta bắt đầu từ mô hình OSI và TCP/IP.

## Các khái niệm chính
- IP: địa chỉ định danh thiết bị
- DNS: dịch tên miền thành IP
- Switch, Router: thiết bị chuyển tiếp gói tin
- NAT: chia sẻ một IP công cộng cho nhiều máy trong LAN

Phần sau sẽ nói về HTTP và Web.', NULL, 51),
(3, 'LESSON', 'Bảo mật Web: tấn công và phòng thủ', 'Đi sâu vào các lỗ hổng phổ biến: SQL Injection, XSS, CSRF, cùng cách phòng chống trong thực tế.', 'user7', 'Công nghệ Thông tin', 'INTERMEDIATE', 90,
'
# Bảo mật Web

Các lỗ hổng hàng đầu trong ứng dụng web thường đến từ việc tin tưởng dữ liệu đầu vào.

## Các tấn công phổ biến
- SQL Injection: dùng câu lệnh chuẩn bị sẵn (PreparedStatement) để chống
- XSS: mã hóa nội dung người dùng trước khi render
- CSRF: dùng token và kiểm tra Origin header

## Nguyên tắc phòng thủ
1. Không bao giờ tin dữ liệu từ client
2. Validate mọi đầu vào
3. Phân quyền tối thiểu

Đừng quên cập nhật dependency thường xuyên để tránh lỗ hổng đã công bố.', NULL, 64),
(4, 'LESSON', 'Tối ưu hiệu năng cơ sở dữ liệu PostgreSQL', 'Các kỹ thuật tối ưu truy vấn: chỉ mục B-tree và GIN, phân tích SQL với EXPLAIN, chống N+1 và tuning cấu hình.', 'user13', 'Công nghệ Thông tin', 'ADVANCED', 120,
'
# Tối ưu PostgreSQL

Khi dữ liệu lớn dần, truy vấn chậm dần là điều tất yếu. Việc đầu tiên là đọc EXPLAIN ANALYZE.

## Checklist tối ưu
- Đánh chỉ mục đúng cột lọc và sắp xếp
- Tránh hàm bọc quanh cột đã đánh chỉ mục
- Dùng GIN cho JSONB và full-text search
- Tắt autocommit, gộp batch insert
- Tăng shared_buffers và work_mem hợp lý theo RAM

## Ví dụ
EXPLAIN ANALYZE SELECT * FROM feed_item WHERE type = ''LESSON'' ORDER BY source_created_at DESC;

Sau khi tối ưu, truy vấn trang feed giảm từ vài trăm ms xuống dưới 20ms.', NULL, 89),
(5, 'LESSON', 'Giới thiệu Git và quy trình làm việc nhóm', 'Hướng dẫn các thao tác Git cơ bản: clone, commit, branch, merge, rebase và quy trình làm việc nhóm với pull request.', 'user2', 'Kỹ thuật Phần mềm', 'BEGINNER', 50,
'
# Git cho người mới

Git giúp theo dõi lịch sử thay đổi code và phối hợp nhóm.

## Các lệnh căn bản
- git clone: kéo repo về máy
- git add + git commit: lưu một phiên bản
- git branch: tạo nhánh làm việc
- git merge: gộp nhánh

Quy trình đề xuất cho team nhỏ: mỗi feature một nhánh, commit message rõ ràng, merge qua pull request sau khi review.', NULL, 43),
(6, 'LESSON', 'Thiết kế REST API tốt với Spring Boot', 'Nguyên tắc RESTful, cấu trúc resource, xử lý lỗi chuẩn, versioning và cách tổ chức controller, service, repository.', 'user4', 'Kỹ thuật Phần mềm', 'INTERMEDIATE', 75,
'
# REST API với Spring Boot

Một REST API tốt phải nhất quán về tài nguyên, trạng thái HTTP và định dạng lỗi.

## Nguyên tắc
- Dùng danh từ số nhiều cho resource: /api/knowledge
- HTTP method đúng ngữ nghĩa: GET, POST, PUT, PATCH, DELETE
- Trả về HTTP status phù hợp: 200, 201, 204, 400, 404

## Xử lý lỗi
Tạo @RestControllerAdvice trả về body chuẩn có mã lỗi, message và timestamp thay vì stack trace lộ ra ngoài.', NULL, 70),
(7, 'LESSON', 'Clean Code: viết code dễ đọc và bảo trì', 'Áp dụng tên biến có nghĩa, hàm ngắn, tránh Magic Number và nguyên tắc SRP để code dễ đọc và dễ sửa.', 'user9', 'Kỹ thuật Phần mềm', 'INTERMEDIATE', 80,
'
# Clean Code

Code được đọc nhiều hơn viết. Hãy ưu tiên độ rõ ràng.

## Quy tắc vàng
- Tên biến, hàm mô tả đúng mục đích
- Mỗi hàm nên làm đúng một việc
- Tránh lặp code, tách hằng số có tên
- Comment giải thích lý do, không giải thích cách làm

Kết quả: ít bug hơn, team mới onboarding nhanh hơn, chi phí bảo trì thấp hơn rõ rệt.', NULL, 58),
(8, 'LESSON', 'Kiến trúc Microservice và bài toán phân rã', 'Cách tách monolithic thành microservice: phân rã theo bounded context, giao tiếp sync/async, và cái giá phải trả.', 'user15', 'Kỹ thuật Phần mềm', 'ADVANCED', 110,
'
# Kiến trúc Microservice

Microservice giúp team vận hành độc lập nhưng đi kèm độ phức tạp phân tán.

## Cách phân rã
- Phân rã theo nghiệp vụ (bounded context) chứ không theo lớp kỹ thuật
- Mỗi service tự quản lý database riêng
- Giao tiếp qua API đồng bộ hoặc sự kiện bất đồng bộ

## Cái giá phải trả
- Distributed transaction phức tạp, dùng event-driven, outbox pattern
- Theo dõi log, trace, monitoring khó hơn nhiều

Chỉ nên microservice khi team và sản phẩm đủ lớn.', NULL, 96),
(9, 'LESSON', 'Cơ sở Toán cho Machine Learning', 'Ôn lại đại số tuyến tính, vi phân, xác suất là nền tảng cần thiết để hiểu thuật toán học máy.', 'user5', 'Khoa học Máy tính', 'INTERMEDIATE', 95,
'
# Toán cho Machine Learning

Hầu hết thuật toán học máy đều xoay quanh ba trụ cột toán học.

## Ba trụ cột
- Đại số tuyến tính: vector, ma trận, tích vô hướng, biến đổi tuyến tính
- Vi phân: gradient, đạo hàm riêng, kỹ thuật gradient descent
- Xác suất: phân phối, kỳ vọng, định lý Bayes

Hiểu bản chất toán giúp bạn chọn mô hình và đọc paper dễ hơn là học vẹt công thức.', NULL, 82),
(10, 'LESSON', 'Xử lý ngôn ngữ tự nhiên cơ bản', 'Từ tokenization, loại bỏ stopword, TF-IDF đến mô hình word embedding và ứng dụng phân loại văn bản.', 'user8', 'Khoa học Máy tính', 'BEGINNER', 70,
'
# NLP cơ bản

Xử lý ngôn ngữ tự nhiên giúp máy hiểu văn bản con người.

## Các bước tiền xử lý
- Tokenization: tách câu thành token
- Loại bỏ stopword và ký tự thừa
- Chuyển về dạng gốc (stemming, lemmatization)

## Biểu diễn văn bản
- TF-IDF: đánh trọng số từ dựa trên tần suất
- Word embedding: biểu diễn từ dưới dạng vector dày đặc

Ví dụ minh họa: phân loại ý kiến tích cực và tiêu cực.', NULL, 47),
(11, 'LESSON', 'Giải thuật tìm kiếm và sắp xếp', 'So sánh các thuật toán tìm kiếm nhị phân, sắp xếp nổi bọt, chèn, merge sort và quick sort về độ phức tạp.', 'user11', 'Khoa học Máy tính', 'BEGINNER', 55,
'
# Tìm kiếm và sắp xếp

Sắp xếp tốt giúp tìm kiếm nhanh. Hãy nhìn vào độ phức tạp.

## Sắp xếp
- Nổi bọt, chèn: O(n^2) nhưng đơn giản
- Merge sort: O(n log n) ổn định
- Quick sort: trung bình O(n log n), nhanh trong thực tế

## Tìm kiếm
Tìm kiếm nhị phân trên mảng đã sắp xếp chỉ mất O(log n).

Chọn thuật toán phù hợp với kích thước dữ liệu thực tế.', NULL, 39),
(12, 'LESSON', 'Học sâu với Mạng nơ-ron tích chập', 'Kiến trúc CNN: convolution, pooling, fully connected, cùng cách huấn luyện tránh overfitting.', 'user17', 'Khoa học Máy tính', 'ADVANCED', 130,
'
# Mạng nơ-ron tích chập

CNN là lựa chọn hàng đầu cho bài toán ảnh.

## Các thành phần
- Convolution: trích đặc trưng cục bộ bằng bộ lọc
- Pooling: giảm chiều, tăng bất biến dịch chuyển
- Fully connected: kết hợp đặc trưng để phân loại

## Mẹo huấn luyện
- Dùng data augmentation nếu dữ liệu ít
- Dropout và BatchNorm giúp chống overfitting
- Giảm learning rate theo lịch trình

Thực hành: train nhận diện chữ số trên MNIST với thư viện PyTorch.', NULL, 104),
(13, 'LESSON', 'Đạo hàm và ứng dụng khảo sát hàm số', 'Ôn tập khái niệm đạo hàm, quy tắc tính và cách dùng đạo hàm để tìm cực trị, vẽ bảng biến thiên.', 'user6', 'Toán học', 'BEGINNER', 60,
'
# Đạo hàm và ứng dụng

Đạo hàm là công cụ đo tốc độ thay đổi tức thời của hàm số.

## Quy tắc cơ bản
- Đạo hàm của tổng: (u + v)'''' = u'''' + v''''
- Đạo hàm của tích: (u.v)'''' = u''''.v + u.v''''
- Đạo hàm hàm hợp: (f(g(x)))'''' = f''''(g(x)).g''''(x)

## Ứng dụng khảo sát
- f''''(x) > 0 trên khoảng nào thì hàm đồng biến trên đó
- f''''(x) đổi dấu qua x0 thì x0 là điểm cực trị

Áp dụng tìm cực trị rồi vẽ bảng biến thiên để khảo sát hàm bậc ba.', NULL, 44),
(14, 'LESSON', 'Ma trận và định thức đại số tuyến tính', 'Các phép toán trên ma trận, tính định thức, tìm ma trận nghịch đảo và giải hệ phương trình tuyến tính.', 'user10', 'Toán học', 'BEGINNER', 65,
'
# Ma trận và định thức

Đại số tuyến tính là ngôn ngữ của máy tính trong nhiều bài toán.

## Phép toán ma trận
- Cộng trừ, nhân ma trận theo quy tắc dòng nhân cột
- Ma trận đơn vị là phần tử trung lập
- Ma trận nghịch đảo A^-1 thỏa A.A^-1 = I

## Định thức
- Định thức khác 0 thì ma trận khả nghịch
- Dùng Gauss hoặc khai triển Laplace để tính

Giải hệ phương trình tuyến tính bằng Cramer hoặc Gauss-Jordan minh họa ứng dụng trực tiếp.', NULL, 53),
(15, 'LESSON', 'Xác suất thống kê cho kỹ sư', 'Nắm vững xác suất có điều kiện, định lý Bayes, biến ngẫu nhiên và các phân phối thông dụng.', 'user12', 'Toán học', 'INTERMEDIATE', 85,
'
# Xác suất thống kê

Xác suất giúp ta mô hình hóa sự không chắc chắn.

## Khái niệm chính
- Xác suất có điều kiện P(A|B)
- Định lý Bayes: cập nhật niềm tin khi có dữ liệu mới
- Biến ngẫu nhiên rời rạc và liên tục

## Phân phối thông dụng
- Nhị thức, Poisson cho biến đếm
- Chuẩn (Normal) xuất hiện ở khắp nơi nhờ định lý giới hạn trung tâm

Kết bài bằng cách dùng thống kê để kiểm định giả thuyết trong thực tế.', NULL, 61),
(16, 'LESSON', 'Chuỗi Fourier và biến đổi Laplace', 'Khai triển hàm tuần hoàn thành chuỗi Fourier và dùng biến đổi Laplace giải phương trình vi phân.', 'user18', 'Toán học', 'ADVANCED', 100,
'
# Chuỗi Fourier và Laplace

Hai công cụ biến đổi này là nền tảng của xử lý tín hiệu và mạch điện tử.

## Chuỗi Fourier
Phân tích hàm tuần hoàn thành tổng các dao động hình sin cơ bản.

## Biến đổi Laplace
- Biến đổi hàm theo thời gian sang miền tần số phức: F(s)
- Biến đạo hàm thành phép nhân, giúp giải phương trình vi phân đại số hóa

Ứng dụng: phân tích mạch RLC và hệ thống điều khiển tự động.', NULL, 77),
(17, 'LESSON', 'Động lực học chất điểm', 'Ba định luật Newton, lực ma sát, lực hấp dẫn và bài toán chuyển động thẳng biến đổi đều.', 'user14', 'Vật lý', 'BEGINNER', 50,
'
# Động lực học chất điểm

Các định luật Newton mô tả mối quan hệ giữa lực và chuyển động.

## Ba định luật Newton
- Định luật 1: vật giữ nguyên trạng thái chuyển động nếu không có lực
- Định luật 2: F = m.a
- Định luật 3: lực và phản lực ngược chiều, cùng độ lớn

## Các loại lực
- Lực ma sát nghỉ và ma sát trượt
- Lực hấp dẫn tuân theo công thức của Newton

Minh họa bằng bài toán xe trượt trên mặt phẳng nghiêng.', NULL, 40),
(18, 'LESSON', 'Điện từ trường và mạch điện cơ bản', 'Định luật Ohm, Kirchhoff, từ trường của dòng điện và nguyên lý cảm ứng điện từ.', 'user16', 'Vật lý', 'INTERMEDIATE', 90,
'
# Điện từ trường và mạch điện

Mạch điện và từ trường là nền tảng của hầu hết thiết bị điện tử.

## Mạch điện
- Định luật Ohm: U = R.I
- Định luật Kirchhoff dòng: tổng dòng vào một nút bằng 0

## Từ trường
- Dòng điện tạo từ trường theo quy tắc bàn tay phải
- Cảm ứng điện từ: từ thông biến thiên sinh suất điện động

Ứng dụng: giải bài toán mạch RLC nối tiếp và nguyên lý biến áp.', NULL, 66),
(19, 'LESSON', 'Cơ học lượng tử - nhập môn', 'Sóng và hạt, hàm sóng, nguyên lý bất định Heisenberg và ý nghĩa vật lý của xác suất.', 'admin', 'Vật lý', 'ADVANCED', 105,
'
# Nhập môn Cơ học lượng tử

Ở thang vi mô, thế giới không còn tuân theo trực giác cổ điển.

## Những ý tưởng nền tảng
- Lưỡng tính sóng hạt: electron vừa là sóng vừa là hạt
- Hàm sóng mô tả trạng thái và bình phương modul cho xác suất
- Nguyên lý bất định: không thể đồng thời biết chính xác vị trí và động lượng

Cơ học lượng tử là nền tảng của chất bán dẫn và laser dùng hằng ngày.', NULL, 93),
(20, 'LESSON', 'Nhiệt động lực học cho người mới', 'Khái niệm nhiệt, công, nội năng, định luật 1 và 2 nhiệt động lực học, nguyên lý entropy.', 'user19', 'Vật lý', 'BEGINNER', 55,
'
# Nhiệt động lực học

Nhiệt động lực học nghiên cứu năng lượng và sự chuyển hóa của nó.

## Các định luật
- Định luật 1: năng lượng được bảo toàn, ΔU = Q - A
- Định luật 2: entropy của hệ cô lập không giảm

## Khái niệm
- Nội năng phụ thuộc nhiệt độ
- Công và nhiệt là hai hình thức truyền năng lượng

Minh họa bằng chu trình của động cơ nhiệt.', NULL, 36),
(21, 'LESSON', 'Kinh tế vi mô: cung cầu và thị trường', 'Mô hình cung cầu, trạng thái cân bằng, độ co giãn và ảnh hưởng của thuế lên thị trường.', 'user1', 'Kinh tế', 'BEGINNER', 50,
'
# Kinh tế vi mô: cung cầu

Cung cầu là mô hình giải thích giá cả và sản lượng trên thị trường.

## Khái niệm
- Cầu: lượng muốn mua ở mỗi mức giá
- Cung: lượng muốn bán ở mỗi mức giá
- Trạng thái cân bằng: cung bằng cầu

## Độ co giãn
Độ co giãn của cầu theo giá quyết định ai chịu gánh nặng thuế nhiều hơn.

Ví dụ thực tế: giá xăng tăng làm đổi thay quyết định tiêu dùng.', NULL, 49),
(22, 'LESSON', 'Kinh tế vĩ mô: GDP, lạm phát, thất nghiệp', 'Cách đo GDP, phân biệt lạm phát và lan tỏa, đường Phillips và vai trò chính sách tài khóa, tiền tệ.', 'user2', 'Kinh tế', 'INTERMEDIATE', 65,
'
# Kinh tế vĩ mô

Kinh tế vĩ mô nhìn nền kinh tế ở tầm tổng thể.

## Ba chỉ số quan trọng
- GDP: tổng giá trị sản phẩm cuối cùng trong một kỳ
- Lạm phát: mức tăng chung của giá cả
- Thất nghiệp: tỷ lệ người muốn làm việc nhưng chưa có việc

## Chính sách
- Tài khóa: chi tiêu và thuế của chính phủ
- Tiền tệ: lãi suất và lượng cung tiền của ngân hàng trung ương

Bài sau sẽ nói về thương mại quốc tế và tỷ giá.', NULL, 59),
(23, 'LESSON', 'Phân tích tài chính doanh nghiệp', 'Đọc báo cáo tài chính, tính các chỉ số thanh khoản, khả năng sinh lời và đòn bẩy để đánh giá sức khỏe doanh nghiệp.', 'user20', 'Kinh tế', 'ADVANCED', 95,
'
# Phân tích tài chính doanh nghiệp

Báo cáo tài chính là bức tranh của doanh nghiệp qua các con số.

## Ba báo cáo chính
- Bảng cân đối kế toán: tài sản, nợ, vốn chủ sở hữu
- Báo cáo kết quả kinh doanh: doanh thu, chi phí, lợi nhuận
- Báo cáo lưu chuyển tiền tệ: dòng tiền thực tế

## Chỉ số quan trọng
- Thanh khoản hiện hành: tài sản ngắn hạn chia nợ ngắn hạn
- ROE: lợi nhuận trên vốn chủ sở hữu
- Tỷ số nợ: mức độ dùng đòn bẩy tài chính

So sánh chỉ số cùng ngành để đọc vị trí tương đối của công ty.', NULL, 85),
(24, 'LESSON', 'Quản trị chiến lược căn bản', 'Quy trình hoạch định chiến lược: phân tích môi trường, lựa chọn chiến lược và triển khai bằng các mô hình SWOT, PEST.', 'user3', 'Quản trị Kinh doanh', 'BEGINNER', 60,
'
# Quản trị chiến lược

Chiến lược giúp tổ chức đi đúng hướng dài hạn.

## Quy trình
1. Phân tích bên ngoài bằng PEST: chính trị, kinh tế, xã hội, công nghệ
2. Phân tích nội bộ điểm mạnh, điểm yếu
3. Lựa chọn chiến lược và phân bổ nguồn lực
4. Đo lường và điều chỉnh

SWOT là công cụ tổng hợp để đưa ra quyết định chiến lược ban đầu.', NULL, 45),
(25, 'LESSON', 'Marketing số và hành vi khách hàng', 'Customer journey, phân khúc khách hàng, kênh SEO, quảng cáo trả tiền và đo lường hiệu quả marketing.', 'user4', 'Quản trị Kinh doanh', 'INTERMEDIATE', 80,
'
# Marketing số

Marketing số tiếp cận khách hàng trên các nền tảng kỹ thuật số.

## Hành trình khách hàng
- Nhận biết: SEO, quảng cáo, mạng xã hội
- Cân nhắc: nội dung, đánh giá, so sánh
- Quyết định: ưu đãi, chốt sale

## Đo lường
- Conversion rate: tỷ lệ chuyển đổi
- CAC: chi phí thu hút một khách hàng
- LTV: giá trị lâu dài của khách hàng

Kênh tốt không phải kênh rẻ nhất mà kênh có LTV/CAC cao nhất.', NULL, 68),
(26, 'LESSON', 'Quản lý dự án theo tiêu chuẩn PMI', 'Các quy trình khởi tạo, lập kế hoạch, triển khai, theo dõi và đóng dự án theo PMBOK.', 'user5', 'Quản trị Kinh doanh', 'INTERMEDIATE', 90,
'
# Quản lý dự án theo PMI

Quản lý dự án đưa công việc về đúng phạm vi, thời gian và ngân sách.

## Các quy trình chính
- Khởi tạo: xác định mục tiêu, charter
- Lập kế hoạch: WBS, tiến độ, ngân sách, rủi ro
- Triển khai và theo dõi: điều phối, báo cáo tiến độ
- Đóng dự án: bàn giao và đúc kết bài học

## Bộ ba ràng buộc
Phạm vi, thời gian, chi phí luôn tác động lẫn nhau.

Vai trò của PM là cân bằng và minh bạch trong truyền thông.', NULL, 74),
(27, 'LESSON', 'Luyện nghe IELTS cho người mới bắt đầu', 'Phương pháp luyện nghe hiệu quả: nghe lấy ý chính, dictation, xử lý bẫy trong phần thi IELTS Listening.', 'user7', 'Tiếng Anh', 'BEGINNER', 45,
'
# Luyện nghe IELTS

Nghe là kỹ năng cần luyện đều mỗi ngày thay vì học nước rút.

## Phương pháp
- Nghe lấy ý chính trước, không lo từng từ
- Dictation 5 phút mỗi ngày cải thiện nhanh
- Chú ý bẫy: đổi số, đảo ý kiến, người nói sửa lại thông tin

## Chiến thuật khi thi
- Đọc câu hỏi trước, dự đoán loại từ cần điền
- KJlot hightlight keyword quan trọng
- Không dừng lại ở câu đã bỏ lỡ

Luyện đều 30 phút mỗi ngày hiệu quả hơn 3 tiếng một lần mỗi tuần.', NULL, 42),
(28, 'LESSON', 'Viết luận tiếng Anh học thuật', 'Cấu trúc essay 5 đoạn, cách viết câu chủ đề, triển khai luận điểm và tránh lỗi ngữ pháp phổ biến.', 'user9', 'Tiếng Anh', 'INTERMEDIATE', 70,
'
# Viết luận học thuật

Một bài luận tốt có luận điểm rõ và cấu trúc nhất quán.

## Cấu trúc 5 đoạn
- Introduction: hook, background, thesis statement
- Body: mỗi đoạn một luận điểm, có ví dụ và giải thích
- Conclusion: tóm tắt và ý mở rộng

## Mẹo viết
- Viết câu chủ đề (topic sentence) rõ ràng
- Liên kết đoạn bằng linking words
- Đọc lại để sửa lỗi chạy câu (run-on sentence)

Thực hành mỗi tuần một bài và nhờ người khác góp ý để tiến bộ nhanh.', NULL, 63),
(29, 'LESSON', 'Phát âm chuẩn IPA và nối âm', 'Làm quen bảng IPA, các âm khó với người Việt và quy tắc nối âm, nuốt âm khi nói tự nhiên.', 'user11', 'Tiếng Anh', 'BEGINNER', 40,
'
# Phát âm IPA và nối âm

Phát âm chuẩn giúp người nghe hiểu bạn dễ dàng hơn.

## Bắt đầu với IPA
- Phân biệt âm ngắn và dài: i và i:, u và u:
- Các âm khó với người Việt: th, æ, ʌ, ɜ:
- Phụ âm cuối cần bật rõ hoặc ngừng hẳn

## Nối âm
- Phụ âm nối sang nguyên âm: an apple đọc thành a-napple
- Nhấn trọng âm từ và nhịp điệu câu

Ghi âm và so sánh với người bản ngữ là cách phản hồi tốt nhất.', NULL, 38),
(30, 'LESSON', 'Từ vựng chuyên ngành công nghệ', 'Bộ từ vựng và cụm từ thông dụng trong công việc lập trình, họp nhóm và đọc tài liệu kỹ thuật tiếng Anh.', 'user15', 'Tiếng Anh', 'INTERMEDIATE', 50,
'
# Từ vựng chuyên ngành công nghệ

Từ vựng công nghệ giúp bạn đọc tài liệu và họp nhóm quốc tế tự tin hơn.

## Các cụm từ hay dùng
- Tech debt, code review, push to production
- Root cause, workaround, deprecated feature
- Milestone, sprint, standup meeting

## Mẹo học
- Học theo cụm (collocation) thay vì từ đơn
- Đọc tài liệu tiếng Anh thường xuyên
- Ghi chú lại cụm từ xuất hiện trong họp

Sau 3 tháng đều đặn, họp tiếng Anh sẽ không còn là áp lực.', NULL, 55),
(31, 'QUESTION', 'Vì sao HashSet không duy trì thứ tự chèn trong Java?', 'Thắc mắc về cách HashSet lưu trữ phần tử và vì sao thứ tự lặp lại không giống thứ tự thêm vào.', 'user6', 'Công nghệ Thông tin', NULL, NULL,
'
Mình mới học Java, thấy khi add các phần tử vào HashSet theo thứ tự 3, 1, 2 nhưng khi in ra thứ tự lại thay đổi. Vì sao HashSet không giữ thứ tự chèn vậy? Có cấu trúc nào vừa duy nhất vừa giữ thứ tự không?', FALSE, 34),
(32, 'QUESTION', 'Nên dùng VARCHAR hay TEXT cho cột trong PostgreSQL?', 'Băn khoăn khi thiết kế bảng, nên chọn kiểu dữ liệu nào cho các cột nội dung dài không xác định trước.', 'user10', 'Công nghệ Thông tin', NULL, NULL,
'
Khi thiết kế bảng trong PostgreSQL, mình phân vân giữa VARCHAR(n) và TEXT cho cột mô tả, nội dung bài viết. Nên dùng kiểu nào? VARCHAR(255) có hiệu năng tốt hơn TEXT không?', FALSE, 46),
(33, 'QUESTION', 'Cách debug lỗi 500 khi deploy Spring Boot lên server', 'Gặp lỗi 500 sau khi deploy, đã kiểm tra log nhưng chưa rõ hướng xử lý, cần người chỉ cách xác định nguyên nhân.', 'user14', 'Công nghệ Thông tin', NULL, NULL,
'
Sau khi deploy Spring Boot lên server Linux, một số API trả về 500. Mình đã xem log nhưng quá dài. Nên bắt đầu kiểm tra từ đâu và có công cụ nào giúp tìm lỗi nhanh không?', TRUE, 72),
(34, 'QUESTION', 'Khác nhau giữa @Component, @Service, @Repository trong Spring?', 'Cảm thấy cả ba annotation đều đăng ký bean, không rõ khác biệt thực sự và khi nào nên dùng loại nào.', 'user3', 'Kỹ thuật Phần mềm', NULL, NULL,
'
Mình thấy @Component, @Service, @Repository đều giúp Spring phát hiện bean khi scan. Vậy khác biệt thực sự giữa chúng là gì và có nên dùng đúng loại cho từng tầng không?', FALSE, 41),
(35, 'QUESTION', 'Git merge hay rebase nên dùng cái nào cho team nhỏ?', 'Tranh luận trong team về việc dùng merge hay rebase giữ cho lịch sử commit sạch, cần lời khuyên phù hợp team nhỏ.', 'user9', 'Kỹ thuật Phần mềm', NULL, NULL,
'
Team mình có 5 người, đang tranh luận giữa merge và rebase để giữ lịch sử trực quan. Nên áp dụng vài chiến lược nào để vừa sạch lịch sử vừa tránh rủi ro khi push?', FALSE, 52),
(36, 'QUESTION', 'Cấu trúc thư mục nào tốt cho dự án Spring Boot?', 'Muốn tham khảo cách tổ chức package theo module nghiệp vụ thay vì theo lớp kỹ thuật, cần ví dụ cụ thể.', 'user18', 'Kỹ thuật Phần mềm', NULL, NULL,
'
Mình thấy hai kiểu tổ chức package: theo lớp (controller, service, repository) và theo module nghiệp vụ (user, order, payment). Với dự án trung bình nên chọn kiểu nào và cấu trúc ra sao để dễ bảo trì?', TRUE, 67),
(37, 'QUESTION', 'Big-O của Quick Sort trong trường hợp xấu nhất là bao nhiêu?', 'Cần kiểm chứng lại độ phức tạp của Quick Sort khi pivot chọn không tốt và cách khắc phục.', 'user2', 'Khoa học Máy tính', NULL, NULL,
'
Mình biết Quick Sort trung bình O(n log n) nhưng đọc có chỗ nói trường hợp xấu nhất là O(n^2). Khi nào xảy ra và làm sao chọn pivot để tránh?', FALSE, 44),
(38, 'QUESTION', 'Làm sao để tránh overfitting khi huấn luyện mô hình?', 'Mô hình validation điểm cao nhưng test lại kém, cần các kỹ thuật chống overfitting phổ biến.', 'user7', 'Khoa học Máy tính', NULL, NULL,
'
Mô hình của mình đạt rất cao trên tập train nhưng kém trên tập test. Mình đã thử tăng dữ liệu. Còn kỹ thuật nào khác để chống overfitting như regularization hay early stopping không?', FALSE, 58),
(39, 'QUESTION', 'Convolutional Neural Network hoạt động như thế nào?', 'Chưa hiểu rõ vai trò của các lớp convolution, pooling trong CNN và cách nó học đặc trưng từ ảnh.', 'user12', 'Khoa học Máy tính', NULL, NULL,
'
Mình mới học về CNN. Lớp convolution làm gì, vì sao cần pooling và quá trình học đặc trưng từ ảnh diễn ra ra sao qua từng lớp?', TRUE, 79),
(40, 'QUESTION', 'Cách chứng minh bất đẳng thức Cauchy-Schwarz?', 'Cần hướng dẫn từng bước chứng minh bất đẳng thức Cauchy-Schwarz cho hai dãy số thực.', 'user1', 'Toán học', NULL, NULL,
'
Cho hai dãy số thực, mình muốn chứng minh bất đẳng thức Cauchy-Schwarz một cách trọn vẹn. Có cách chứng minh nào dễ hiểu nhất dùng tam thức bậc hai không?', FALSE, 36),
(41, 'QUESTION', 'Đạo hàm riêng dùng để làm gì trong tối ưu?', 'Muốn hiểu vai trò của đạo hàm riêng và gradient trong thuật toán gradient descent.', 'user8', 'Toán học', NULL, NULL,
'
Khi tối ưu hàm nhiều biến, mình thấy hay nhắc đến gradient. Đạo hàm riêng theo từng biến và gradient liên hệ thế nào với hướng tăng, giảm của hàm số?', FALSE, 30),
(42, 'QUESTION', 'Giải hệ phương trình tuyến tính bằng Gauss-Jordan', 'Cần ví dụ từng bước khử Gauss-Jordan để giải hệ ba phương trình ba ẩn.', 'user16', 'Toán học', NULL, NULL,
'
Mình học phương pháp Gauss-Jordan nhưng chưa thạo thao tác biến đổi sơ cấp. Ai giúp mình trình bày từng bước giải hệ 3 phương trình 3 ẩn dưới đây bằng Gauss-Jordan được không?', TRUE, 49),
(43, 'QUESTION', 'Định luật II Newton và lực ma sát nghỉ là gì?', 'Hỏi về điều kiện vật đứng yên trên mặt phẳng nghiêng, vai trò của lực ma sát nghỉ.', 'user5', 'Vật lý', NULL, NULL,
'
Một vật nằm trên mặt phẳng nghiêng không trượt. Theo định luật II Newton, hợp lực bằng 0 nhưng vì sao vật vẫn có thể bắt đầu trượt khi tăng góc nghiêng? Lực ma sát nghỉ đóng vai trò gì?', FALSE, 39),
(44, 'QUESTION', 'Tại sao điện áp xoay chiều truyền tải lại cao?', 'Tìm hiểu lý do truyền tải điện năng ở điện áp cao dù gây nguy hiểm hơn.', 'user13', 'Vật lý', NULL, NULL,
'
Mình đọc thấy điện năng truyền tải thường ở điện áp rất cao như 220kV, 500kV. Vì sao lại chọn điện áp cao khi nó nguy hiểm hơn? Công thức hao phí giải thích điều này thế nào?', TRUE, 55),
(45, 'QUESTION', 'Chi phí cơ hội là gì? Cho ví dụ minh họa', 'Cần giải thích khái niệm chi phí cơ hội trong kinh tế học kèm ví dụ gần gũi.', 'user4', 'Kinh tế', NULL, NULL,
'
Mình mới học kinh tế vi mô, chưa hiểu chi phí cơ hội nghĩa là gì. Có thể giải thích đơn giản kèm một ví dụ thực tế về việc chọn học đại học hay đi làm sớm được không?', FALSE, 33),
(46, 'QUESTION', 'Chỉ số CPI đo lường gì và hạn chế của nó?', 'Hỏi về cách tính CPI, ý nghĩa và những hạn chế khi dùng CPI đánh giá lạm phát.', 'user11', 'Kinh tế', NULL, NULL,
'
Chỉ số giá tiêu dùng CPI được tính thế nào và có những hạn chế gì khi dùng nó để đo lạm phát? Vì sao cảm nhận của người dân đôi khi khác số liệu CPI?', FALSE, 42),
(47, 'QUESTION', 'SWOT phân tích lợi ích thực tế cho startup?', 'Người sáng lập startup nhỏ muốn biết SWOT có thực sự hữu ích không và làm sao dùng cho đúng.', 'user17', 'Quản trị Kinh doanh', NULL, NULL,
'
Startup của mình đang định sản phẩm đầu tiên. Nhiều người nói SWOT lỗi thời, nhiều người vẫn dùng. Với startup nhỏ, SWOT thực sự giúp gì và nên kết hợp với công cụ nào để quyết định hướng đi?', FALSE, 47),
(48, 'QUESTION', 'KPI khác gì OKR trong quản lý hiệu suất?', 'Cần phân biệt KPI và OKR, cách chọn bộ chỉ số phù hợp cho đội sản phẩm.', 'user19', 'Quản trị Kinh doanh', NULL, NULL,
'
Team mình đang chuyển từ KPI sang OKR nhưng vẫn mơ hồ khác biệt. KPI và OKR bổ sung hay loại trừ nhau? Nên áp dụng thế nào cho đội sản phẩm phần mềm?', FALSE, 51),
(49, 'QUESTION', 'Cách cải thiện kỹ năng Speaking IELTS band 6?', 'Học viên đang ở band 5.5 muốn lộ trình lên band 6 trong 2 tháng.', 'user20', 'Tiếng Anh', NULL, NULL,
'
Mình thi thử Speaking đạt 5.5 và cần band 6.0 sau 2 tháng. Nên tập trung vào tiêu chí nào trước, luyện mẫu câu gì và cách tự đánh giá phần nói của mình ra sao?', FALSE, 37),
(50, 'QUESTION', 'Phân biệt past simple và present perfect khi nào?', 'Hỏi về cách chọn giữa hai thì khi nói về việc đã xảy ra trong quá khứ.', 'user15', 'Tiếng Anh', NULL, NULL,
'
Khi kể việc đã xảy ra trong quá khứ, khi nào dùng past simple, khi nào dùng present perfect? Mình thấy I visited và I have visited đều có vẻ đúng, khác biệt nằm ở đâu?', TRUE, 43);

-- =====================================================
-- 2. KNOWLEDGE
-- =====================================================
INSERT INTO knowledge (type, title, "abstract", thumbnail_url, owner_id, category_id, views_count, created_at)
SELECT
    i.type,
    i.title,
    i.abstract,
    NULL,
    u.id,
    c.id,
    i.views,
    now() - (i.n * interval '2 hours')
FROM seed_item i
JOIN users u ON u.username = i.owner_username
JOIN category c ON c.name = i.category_name
WHERE NOT EXISTS (
    SELECT 1 FROM knowledge k
    WHERE k.title = i.title AND k.owner_id = u.id
);

-- =====================================================
-- 3. LESSON
-- =====================================================
INSERT INTO lesson (knowledge_id, content_markdown, level, estimate_time_in_minutes)
SELECT k.id, i.body, i.level, i.estimate_time
FROM seed_item i
JOIN knowledge k ON k.title = i.title AND k.type = 'LESSON'
WHERE i.type = 'LESSON'
  AND NOT EXISTS (SELECT 1 FROM lesson l WHERE l.knowledge_id = k.id);

-- =====================================================
-- 4. QUESTION
-- =====================================================
INSERT INTO question (knowledge_id, content, is_resolved)
SELECT k.id, i.body, COALESCE(i.is_resolved, FALSE)
FROM seed_item i
JOIN knowledge k ON k.title = i.title AND k.type = 'QUESTION'
WHERE i.type = 'QUESTION'
  AND NOT EXISTS (SELECT 1 FROM question q WHERE q.knowledge_id = k.id);

-- =====================================================
-- 5. FEED_ITEM (denormalized projection của knowledge)
-- =====================================================
INSERT INTO feed_item (
    knowledge_id, type, owner_id, owner_name, owner_avatar_url,
    title, "abstract", thumbnail_url,
    category_id, category_name,
    views_count,
    type_meta,
    source_created_at, synced_at
)
SELECT
    k.id, k.type, k.owner_id, u.username, u.avatar_url,
    k.title, k."abstract", k.thumbnail_url,
    k.category_id, c.name,
    k.views_count,
    CASE WHEN k.type = 'LESSON'
         THEN jsonb_build_object(
                  'level', l.level,
                  'estimateTimeInMinutes', l.estimate_time_in_minutes,
                  'contentMarkdown', l.content_markdown
              )
         ELSE jsonb_build_object(
                  'isResolved', q.is_resolved,
                  'acceptedAnswerId', NULL,
                  'content', q.content
              )
    END,
    k.created_at, now()
FROM seed_item i
JOIN knowledge k ON k.title = i.title AND k.owner_id = (
    SELECT uu.id FROM users uu WHERE uu.username = i.owner_username
)
JOIN users u ON u.id = k.owner_id
LEFT JOIN category c ON c.id = k.category_id
LEFT JOIN lesson l ON l.knowledge_id = k.id
LEFT JOIN question q ON q.knowledge_id = k.id
WHERE k.type IN ('LESSON', 'QUESTION')
  AND NOT EXISTS (SELECT 1 FROM feed_item f WHERE f.knowledge_id = k.id);

-- =====================================================
-- 6. USER_FEED (fan-out đến followers của owner, bỏ qua owner is_famous)
-- =====================================================
INSERT INTO user_feed (user_id, feed_item_id, fanned_out_at)
SELECT fl.follower_id, f.knowledge_id, f.source_created_at
FROM seed_item i
JOIN knowledge k ON k.title = i.title
JOIN users u ON u.id = k.owner_id AND NOT COALESCE(u.is_famous, FALSE)
JOIN feed_item f ON f.knowledge_id = k.id
JOIN follows fl ON fl.followee_id = k.owner_id
WHERE NOT EXISTS (
    SELECT 1 FROM user_feed uf
    WHERE uf.user_id = fl.follower_id AND uf.feed_item_id = f.knowledge_id
);

-- =====================================================
-- 7. COMMENT (root + reply)
-- =====================================================
INSERT INTO comment (knowledge_id, user_id, user_name, user_avatar_url, content)
SELECT
    e.knowledge_id,
    e.user_id,
    u.username,
    u.avatar_url,
    (ARRAY[
        'Bài viết rất hữu ích, cảm ơn tác giả!',
        'Mình đã áp dụng được ngay, hay quá.',
        'Cho mình hỏi thêm phần này được không?',
        'Giải thích dễ hiểu hơn nhiều tài liệu khác.',
        'Mình thấy ví dụ minh họa rất trực quan.',
        'Chủ đề hay, mong có phần 2 tiếp theo.',
        'Trình bày rõ ràng, logic dễ theo dõi.',
        'Cảm ơn đã chia sẻ, rất tâm huyết!',
        'Mình bookmark lại để học dần.',
        'Câu hỏi ở cuối bài khiến mình phải suy nghĩ.',
        'Nội dung bổ ích cho cả người mới bắt đầu.',
        'Bài viết khá đầy đủ về lý thuyết nền tảng.',
        'Mình từng hiểu sai chỗ này, giờ đã rõ hơn.',
        'Có thể giải thích bằng ví dụ thực tế hơn không?',
        'Phần này trong sách mình đọc chưa kỹ, nhờ bài viết đã rõ.',
        'Rất chi tiết, đáng để đọc lại vài lần.',
        'Mình sẽ chia sẻ bài viết này cho nhóm học tập.',
        'Tác giả trình bày có hệ thống, dễ học.'
    ])[1 + ((e.knowledge_id * 5 + e.j * 7) % 18)] AS content
FROM (
    SELECT k.id AS knowledge_id, gs.j AS j, 2 + ((k.id * 3 + gs.j) % 20) AS user_id
    FROM knowledge k
    JOIN seed_item i ON i.title = k.title
    CROSS JOIN LATERAL generate_series(0, 1 + (k.id % 4)) AS gs(j)
) e
JOIN users u ON u.id = e.user_id
WHERE NOT EXISTS (
    SELECT 1 FROM comment c
    WHERE c.knowledge_id = e.knowledge_id AND c.user_id = e.user_id AND c.content = (
        (ARRAY[
            'Bài viết rất hữu ích, cảm ơn tác giả!',
            'Mình đã áp dụng được ngay, hay quá.',
            'Cho mình hỏi thêm phần này được không?',
            'Giải thích dễ hiểu hơn nhiều tài liệu khác.',
            'Mình thấy ví dụ minh họa rất trực quan.',
            'Chủ đề hay, mong có phần 2 tiếp theo.',
            'Trình bày rõ ràng, logic dễ theo dõi.',
            'Cảm ơn đã chia sẻ, rất tâm huyết!',
            'Mình bookmark lại để học dần.',
            'Câu hỏi ở cuối bài khiến mình phải suy nghĩ.',
            'Nội dung bổ ích cho cả người mới bắt đầu.',
            'Bài viết khá đầy đủ về lý thuyết nền tảng.',
            'Mình từng hiểu sai chỗ này, giờ đã rõ hơn.',
            'Có thể giải thích bằng ví dụ thực tế hơn không?',
            'Phần này trong sách mình đọc chưa kỹ, nhờ bài viết đã rõ.',
            'Rất chi tiết, đáng để đọc lại vài lần.',
            'Mình sẽ chia sẻ bài viết này cho nhóm học tập.',
            'Tác giả trình bày có hệ thống, dễ học.'
        ])[1 + ((e.knowledge_id * 5 + e.j * 7) % 18)]
    )
);

INSERT INTO comment (
    knowledge_id, user_id, user_name, user_avatar_url,
    root_comment_id, reply_to_comment_id, reply_to_user_name, content
)
SELECT
    f.knowledge_id,
    f.user_id,
    u.username,
    u.avatar_url,
    f.root_id,
    f.root_id,
    f.root_user_name,
    (ARRAY[
        'Mình nghĩ bạn nói đúng hướng đó.',
        'Bổ sung thêm: chú ý dữ liệu đầu vào nhé.',
        'Cảm ơn bạn, mình áp dụng thử ngay.',
        'Mình từng gặp lỗi giống vậy.',
        'Hay, mình lưu lại để tham khảo.',
        'Giải thích thêm đoạn này được không?',
        'Chỗ này mình thấy ví dụ là rõ nhất.',
        'Đúng vậy, mình đồng ý với bạn.'
    ])[1 + ((f.knowledge_id * 11 + f.j * 13) % 8)] AS content
FROM (
    SELECT
        k.id AS knowledge_id,
        rc.id AS root_id,
        rc.user_name AS root_user_name,
        gs.j AS j,
        2 + ((k.id * 7 + gs.j * 3) % 20) AS user_id
    FROM knowledge k
    JOIN seed_item i ON i.title = k.title
    JOIN comment rc
        ON rc.knowledge_id = k.id
       AND rc.root_comment_id IS NULL
       AND rc.deleted_at IS NULL
    CROSS JOIN LATERAL generate_series(0, (k.id % 3) - 1) AS gs(j)
) f
JOIN users u ON u.id = f.user_id
WHERE NOT EXISTS (
    SELECT 1 FROM comment c
    WHERE c.knowledge_id = f.knowledge_id
      AND c.root_comment_id = f.root_id
      AND c.content = (
        (ARRAY[
            'Mình nghĩ bạn nói đúng hướng đó.',
            'Bổ sung thêm: chú ý dữ liệu đầu vào nhé.',
            'Cảm ơn bạn, mình áp dụng thử ngay.',
            'Mình từng gặp lỗi giống vậy.',
            'Hay, mình lưu lại để tham khảo.',
            'Giải thích thêm đoạn này được không?',
            'Chỗ này mình thấy ví dụ là rõ nhất.',
            'Đúng vậy, mình đồng ý với bạn.'
        ])[1 + ((f.knowledge_id * 11 + f.j * 13) % 8)]
      )
);

-- Cập nhật reply_count cho comment gốc
UPDATE comment c
SET reply_count = (
    SELECT COUNT(*) FROM comment r
    WHERE r.root_comment_id = c.id AND r.deleted_at IS NULL
)
WHERE c.root_comment_id IS NULL
  AND c.knowledge_id IN (
      SELECT k.id FROM knowledge k JOIN seed_item i ON i.title = k.title
  );

-- =====================================================
-- 8. VOTE (upvote +1 / downvote -1, mỗi user 1 phiếu/bài)
--    Upvote: user1..10 (id 2..11) | Downvote: user11..20 (id 12..21)
-- =====================================================
INSERT INTO vote (knowledge_id, user_id, value)
SELECT u.knowledge_id, u.user_id, u.value
FROM (
    SELECT sk.knowledge_id, 2 + ((sk.knowledge_id * 7 + gs.j * 9) % 10) AS user_id, 1::smallint AS value
    FROM (SELECT k.id AS knowledge_id FROM knowledge k JOIN seed_item i ON i.title = k.title) sk
    CROSS JOIN LATERAL generate_series(0, 1 + (sk.knowledge_id % 9)) AS gs(j)

    UNION ALL

    SELECT sk.knowledge_id, 12 + ((sk.knowledge_id * 11 + gs.j * 3) % 10) AS user_id, (-1)::smallint AS value
    FROM (SELECT k.id AS knowledge_id FROM knowledge k JOIN seed_item i ON i.title = k.title) sk
    CROSS JOIN LATERAL generate_series(0, (sk.knowledge_id % 3) - 1) AS gs(j)
) u
JOIN users x ON x.id = u.user_id
WHERE NOT EXISTS (
    SELECT 1 FROM vote v
    WHERE v.knowledge_id = u.knowledge_id AND v.user_id = u.user_id
);

-- =====================================================
-- 9. ĐỒNG BỘ COUNTER: comment_count, vote_score (knowledge + feed_item)
-- =====================================================
UPDATE knowledge k
SET comment_count = (
        SELECT COUNT(*) FROM comment c
        WHERE c.knowledge_id = k.id AND c.deleted_at IS NULL
    ),
    vote_score = (
        SELECT COALESCE(SUM(v.value), 0) FROM vote v
        WHERE v.knowledge_id = k.id
    )
WHERE EXISTS (SELECT 1 FROM seed_item i WHERE i.title = k.title);

UPDATE feed_item f
SET comment_count = k.comment_count,
    vote_score = k.vote_score
FROM knowledge k
WHERE f.knowledge_id = k.id
  AND EXISTS (SELECT 1 FROM seed_item i WHERE i.title = k.title);

-- =====================================================
-- 10. QUESTION đã giải: gán câu trả lời được chọn + cập nhật type_meta
-- =====================================================
UPDATE question q
SET accepted_answer_id = sub.first_comment_id
FROM (
    SELECT k.id AS kid, MIN(c.id) AS first_comment_id
    FROM knowledge k
    JOIN seed_item i ON i.title = k.title
    JOIN question q2 ON q2.knowledge_id = k.id
    JOIN comment c
        ON c.knowledge_id = k.id
       AND c.root_comment_id IS NULL
       AND c.deleted_at IS NULL
    WHERE k.type = 'QUESTION'
      AND q2.is_resolved = TRUE
      AND q2.accepted_answer_id IS NULL
    GROUP BY k.id
) sub
WHERE q.knowledge_id = sub.kid;

UPDATE feed_item fi
SET type_meta = jsonb_build_object(
        'isResolved', q.is_resolved,
        'acceptedAnswerId', q.accepted_answer_id,
        'content', q.content
    )
FROM question q
JOIN knowledge k ON k.id = q.knowledge_id AND k.type = 'QUESTION'
JOIN seed_item i ON i.title = k.title
WHERE fi.knowledge_id = q.knowledge_id
  AND q.is_resolved = TRUE
  AND q.accepted_answer_id IS NOT NULL;

DROP TABLE IF EXISTS seed_item;