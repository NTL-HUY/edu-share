# Feed — Refactor `getFeed` & Tổ chức Unit Test

> Ngày: 2026-09-11
> Phạm vi: Backend `edushare-backend` — module `feed`
> Liên quan: `FeedServiceImpl`, `FeedQueryService(Impl)` (mới), `FeedCacheService(Impl)` (mới)

---

## 1. Bối cảnh

`getFeed` đã được refactor: logic query (pushed feed → KOL → normal followee → discovery) tách sang `FeedQueryService` (sub-package `feed.query/`), logic Redis cache tách sang `FeedCacheService` (sub-package `feed.cache/`). Cả hai theo đúng convention project: Interface + Impl.

Sau khi refactor, tổ chức lại toàn bộ unit test cho feed trong `src/test`.

---

## 2. Files test mới (trong `src/test`)

| File | Số test | Vị trí package |
|---|---|---|
| `FeedServiceImplTest.java` | 9 | `com.nbh.edushare.modules.feed` |
| `FeedQueryServiceImplTest.java` | 12 | `com.nbh.edushare.modules.feed.query` |
| `FeedCacheServiceImplTest.java` | 9 | `com.nbh.edushare.modules.feed.cache` |
| `FeedCursorTest.java` | 6 | `com.nbh.edushare.modules.feed.util` |

**Kết quả: 36/36 test pass.**

Chạy riêng:

```bash
# từ thư mục edushare-backend
.\mvnw.cmd -Dtest="FeedService*,FeedCacheService*,FeedCursor*" test
```

### 2.1 Nội dung test đã cover

**FeedServiceImplTest**
- `getFeed` với `userId == null` → đi discovery path, cache discovery, không gọi `loadFeedByUserId`
- `getFeed` anonymous/user: cache hit trả thẳng cache, không gọi query service
- `getFeed` cache miss → decode cursor → `loadFeedByUserId` → `putFeed` với đúng TTL
- `searchFeed` → delegate repository + map sang `FeedSearchResult`
- `findProjectedById`, `adjustCounters`, `existsByKnowledgeId` → đúng delegate

**FeedQueryServiceImplTest**
- `loadFeedByUserId`: pushed feed tự đủ limit
- Dedup cross-source theo `knowledgeId` (pushed win qua `putIfAbsent`)
- Fallback 3 tầng: pushed → KOL → normal → discovery, mỗi tầng chỉ fetch `effectiveLimit - pool.size()` còn thiếu
- Discovery fallback khi pool rỗng dùng `List.of(-1L)`; khi có pool thì exclude `pool.keySet()`
- Cursor pagination: dùng variants `findPushedFeed`/`findOlderPublicByOwners`/`findOlderPublicDiscovery` với đúng `(createdAt, id)`
- Sort desc theo `sourceCreatedAt` rồi `knowledgeId`
- `loadDiscoveryFeed`: first-page vs cursor, hasMore, pageable = `limit + 1`

**FeedCacheServiceImplTest**
- Redis get/set bình thường
- Fail-open: Redis down → `getFeed` trả null, `putFeed` không throw
- Cache key format (user vs discovery, cursor null vs có cursor)

**FeedCursorTest**
- encode/decode round-trip
- null/blank → null
- Base64 lỗi / nội dung sai format / id không phải số → `AppException`

---

## 3. Vấn đề logic phát hiện khi test

### 3.1 `nextCursor` luôn được generate dù `hasMore == false`

`FeedQueryServiceImpl.java:74` (cả `loadFeedByUserId` và `loadDiscoveryFeed`):

```java
String nextCursor = finalList.isEmpty() ? null
        : new FeedCursor(finalList.getLast().getSourceCreatedAt(),
                         finalList.getLast().getKnowledgeId()).encode();
```

- 2 test đầu kỳ vọng `nextCursor == null` khi hết dữ liệu → **fail**.
- Đã cập nhật test để assert đúng hành vi hiện tại (cursor luôn trỏ tới item cuối khi list không rỗng).
- **Quyết định cần cân nhắc:** nếu muốn `nextCursor == null` khi `hasMore == false`, phải sửa code (ngoài phạm vi test).

### 3.2 Cross-source duplicate khi `putIfAbsent`

Khi `limit` lớn, pool có thể nhỏ hơn limit thực nếu nhiều nguồn trả trùng ID. Logic `putIfAbsent` dedup ok, không gây lỗi hoạt động — chỉ lưu ý về sizing.

### 3.3 Cache invalidation chưa được giải quyết

- `getFeed`/`getDiscoveryFeed` cache theo `(userId, cursor, limit)` với TTL 30s/60s.
- Không có cơ chế invalidation khi knowledge tạo/sửa/xóa, counters thay đổi, follow thay đổi.
- Đã thảo luận ở các session trước — chưa impl. Đây là vấn đề thiết kế, không test được ở unit level.

---

## 4. Ghi chú thêm

- `FeedServiceImpl` là class **package-private** → test phải nằm cùng package `com.nbh.edushare.modules.feed` (không để trong `feed/service/`).
- Các Impl service mới (`FeedQueryServiceImpl`, `FeedCacheServiceImpl`) là `public` — không nhất quán với convention package-private như các Impl khác trong project. Xem xét đổi lại nếu muốn đồng bộ.