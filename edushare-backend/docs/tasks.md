# Backend Tasks — edushare-backend

> File theo dõi các công việc/cải tiến kỹ thuật. Mỗi task: mô tả vấn đề, cách xử lý, gợi ý triển khai.

---

## TASKS-001 — `isFamous` (cờ KOL/user nổi tiếng) chưa có logic nào bật

**Trạng thái:** 🔲 TODO — chưa code

### Vấn đề

Cờ `is_famous` đã tồn tại ở nhiều tầng nhưng **chưa có bất kỳ chỗ nào set nó thành `true`**, nên toàn bộ nhánh logic KOL đang nằm trong trạng thái "chết":

**Nơi cờ đang được ĐỌC (nhưng không bao giờ bật):**

| Vị trí | Mục đích |
|---|---|
| `User.java:35-36` | Entity có cột `is_famous BOOLEAN NOT NULL DEFAULT FALSE` (migration `V1__Initial_schema.sql`), field mặc định `false` |
| `FollowRepository.java:31-43` | `findFamousFolloweeIds()` / `findNormalFolloweeIds()` — tách 2 nhóm followee KOL-và-thường để xếp hạng feed |
| `FeedServiceImpl.java:90-96` | Feed ưu tiên bài của `famousIds` ngay đầu |
| `KnowledgeFeedListener.java:29-32` | `isFamous` → **skip fan-out** để tránh DB bloat (KOL có rất nhiều follower) |
| `KnowledgeEventMapper.java:20,30` | Bơm `isFamous` vào `KnowledgeCreatedEvent` để feed quyết định ngay từ event |
| `ProfileResponse` / `UserProfileResponse` | Trả cờ ra API cho frontend |

**Kiểm chứng:** grep toàn project chỉ thấy 1 chỗ là khai báo cột/field — **không có `setIsFamous(true)` ở đâu cả**. Không có admin endpoint, không có job tự động, migration seed cũng không.

**Hậu quả hiện tại:**
- Mọi user `isFamous = false` → nhánh feed KOL không bao giờ chạy.
- Fan-out vẫn được thực hiện cho mọi bài viết → khi một user có hàng chục nghìn follower, bài của họ sẽ được ghi vào `user_feed` cho từng follower → **DB bloat** (đúng thứ `KnowledgeFeedListener` đang cố tránh).

### Hướng xử lý (theo độ phức tạp tăng dần)

**Option A — Cấp quyền thủ công (đơn giản, nên làm trước):**
- Thêm endpoint admin để set/unset cờ, vd:
  - REST: `PUT /api/users/{userId}/famous` (chỉ `ADMIN`) — trong `user` module
  - hoặc GraphQL mutation `markUserFamous(userId, isFamous)` trong `UserGraphQLController`
- Service chỉ cần `userRepository.findById` → `setIsFamous(...)` → save.
- **Không cần tạo migration** (cột đã có).

**Option B — Bổ sung seed dữ liệu ban đầu (Flyway):**
- Migration mới, vd `V4__seed_famous_users.sql`, `UPDATE users SET is_famous = TRUE WHERE username IN (...)` dành cho những KOL đầu tiên.
- Lưu ý: chỉ là lối thoát tạm, không phải giải pháp vận hành.

**Option C — Tự động hóa theo ngưỡng follower (recommend về lâu dài):**
- Dùng scheduled job (đã có `SchedulingConfig`): định kỳ (vd mỗi giờ) query `countByFollowee_Id(...)` với mỗi followee vượt ngưỡng (vd ≥ 10.000 follower) → set `isFamous = true`; dưới ngưỡng → `false`.
- Cần index cho cột `followee_id` + `is_famous` (check hiện trạng migration V1).
- Gợi ý lọc trước bằng query partition: `SELECT followee_id, COUNT(*) FROM follow GROUP BY followee_id HAVING COUNT(*) >= :threshold` — không cần chạy per-user.

**Option D — Kết hợp (recommend cuối cùng):** A (quản lý tay) + C (tự động) + một API admin override để support đội vận hành.

### Gợi ý triển khai chi tiết (khi cần code)

1. **`user` module:**
   - `UserRepository` thêm `@Query` đếm follower theo từng user (hoặc dùng `FollowRepository.countByFollowee_Id`).
   - Thêm service method `setFamous(Long userId, boolean famous)` + check `ADMIN` qua `@PreAuthorize` hoặc role check trong service.
   - Với Option C: thêm class `FamousUserSyncJob` (vd `modules/user/FamousUserSyncJob.java`) gọi `@Scheduled(fixedDelay = ...)`.
2. **Đồng bộ ảnh hưởng downstream** (quan trọng):
   - Khi một user **mới thành KOL**: những bài mới đăng sau đó sẽ được `KnowledgeEventMapper` đưa `isFamous=true` vào event → feed tự skip fan-out. ✅ tự nhiên.
   - Nhưng bài **đăng trước khi bật cờ**: `feed_item` và `user_feed` đã được tạo với fan-out cũ — cần quyết định chấp nhận (xử lý dần) hay viết backfill.
   - **Invalidate cache feed**: nhánh `findFamousFolloweeIds` đổi kết quả → cache Redis feed (`feed:user:*`) có thể trả data cũ. Cần xóa cache theo user khi cờ đổi.
3. **Testing:** unit test cho job đếm follower + integration test luồng "user thành KOL → KnowledgeCreatedEvent `isFamous=true` → feed skip fan-out".

### Quyết định cần xác nhận
- Ngưỡng bao nhiêu follower thì coi là KOL?
- Cần admin API để override tay hay chỉ tự động theo ngưỡng?
- Bài đã có trong `user_feed` trước khi bật cờ thì xử lý backfill như thế nào?

---