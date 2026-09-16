# Phân tích kỹ thuật — Lỗi `loadFeedByUserId`: Discovery không hoạt động khi hết bài follow

> Phạm vi: `edushare-backend/src/main/java/com/nbh/edushare/modules/feed`
> Các file liên quan: `query/FeedQueryServiceImpl.java`, `repository/FeedItemRepository.java`, `repository/UserFeedRepository.java`, `util/FeedCursor.java`, `FeedServiceImpl.java`, `FeedGraphQLController.java`, `FeedPage.java`.

---

## 1. Triệu chứng (tái hiện được trên DB thật)

| Trạng thái login | Hành vi trên UI |
|---|---|
| **Đăng nhập (user5, id=6)** | Bảng tin "Bảng tin cá nhân" lướt được **rất ít bài** (~16 bài), nhanh chóng hiện **"Đã hết dữ liệu"** |
| **Chưa đăng nhập** (userId = null → `loadDiscoveryFeed`) | Lướt được **toàn bộ 50 bài** đăng (discovery thuần hoạt động tốt) |

Kỳ vọng hệ thống: **discovery chỉ lấp khi hết bài follow** (không trộn lẫn vào bảng tin khi còn bài follow) — thiết kế hiện tại đúng với kỳ vọng này, nhưng implementation gây lỗi khiến discovery không bao giờ được lấp.

---

## 2. Luồng thực thi hiện tại của `loadFeedByUserId`

`FeedQueryServiceImpl.loadFeedByUserId(userId, cursor, limit)`:

```
effectiveLimit = limit + 1
pool = LinkedHashMap<knowledgeId, FeedItem>   // dedup giữ nguồn đầu tiên

L1. pushed      : user_feed của user (keyset: sourceCreatedAt, knowledgeId)
L2. KOL         : findLatest/OlderPublicByOwners(famousIds)   -- nếu không rỗng
L3. normal      : findLatest/OlderPublicByOwners(normalIds)   -- chỉ nếu pool < effectiveLimit
L4. discovery   : findLatest/OlderPublicDiscovery(exclude=pool, size=effectiveLimit-pool)   -- chỉ nếu pool < effectiveLimit

sort DESC (sourceCreatedAt, knowledgeId)
hasMore = sorted.size() > limit
finalList = hasMore ? sorted.subList(0, limit) : sorted
nextCursor = finalList.getLast()  → (sourceCreatedAt, knowledgeId) mã hoá base64
```

Điểm mấu chốt: **`nextCursor` là vị trí (thời điểm + id) của bài CUỐI trang đã trả, và CÙNG một cursor này được dùng cho TẤT CẢ các tầng layer trong trang kế tiếp.**

Các repository query (keyset pagination):

- `UserFeedRepository.findPushedFeed` — `< cursor` theo `(sourceCreatedAt, knowledgeId)`.
- `FeedItemRepository.findOldestPublicByOwners` — `< cursor`.
- `FeedItemRepository.findOlderPublicDiscovery`:

```sql
WHERE knowledge_id NOT IN :excludeIds
  AND is_public = true
  AND deleted_at IS NULL
  AND ( source_created_at < :createdAt
        OR (source_created_at = :createdAt AND knowledge_id < :id) )
ORDER BY source_created_at DESC, knowledge_id DESC
```

→ **Discovery luôn chỉ được phép lấy các bài CŨ HƠN cursor.**

---

## 3. Nguyên nhân gốc

### 3.1 Cursor dùng chung bị «neo» xuống đáy timeline

Vì discovery là **tầng lấp cuối cùng**, với một user có nhiều bài follow, mỗi trang đều đủ đầy bởi pushed/normal → **discovery không bao giờ được gọi** trong những trang đầu. Cursor vì thế lần lượt trỏ vào bài follow ngày càng cũ.

Khi user theo dõi một người có **bài cũ NHẤT trong toàn hệ thống**, thì đến lúc push cạn, cursor đã nằm **ở đáy timeline**. Lúc này discovery phải lấp, nhưng:

- Toàn bộ discovery còn lại chưa xem đều **MỚI HƠN cursor**;
- `findOlderPublicDiscovery` chỉ cho phép lấy bài **CŨ HƠN cursor**;

→ Discovery trả về ~0 bài, `hasMore = false` → UI hiện "Đã hết dữ liệu" **mặc dù còn 30-40+ bài discovery chưa hề hiển thị**.

### 3.2 Số liệu minh hoạ với user5 (id=6)

DB seed: 50 `feed_item`, tất cả `is_public=true`, `deleted_at IS NULL`.

- user5 có 15 bài pushed (`user_feed`), follow 6 người, 0 followees famous.
- Các layer theo từng trang (SSR page 1 limit=10, sau đó loadMore limit=2):

| Trang | Cursor trước trang | pushed | normal | discovery | Kết quả | hasMore |
|---|---|---|---|---|---|---|
| 1 | null | 11 (đủ effectiveLimit=11) | — | không gọi | 10 bài | true |
| 2 | bài #12 | 3 | — | không gọi | 2 bài | true |
| 3 | bài #20 | 3 | — | không gọi | 2 bài | true |
| 4 | **`(2026-09-11 15:23, id=7)`** — ngang bài cũ nhất hệ thống | 1 | 0 | **chỉ 1 bài cũ hơn cursor** | 1 bài | **false → "Đã hết dữ liệu"** |

Số liệu kiểm chứng trên DB:

- `seen_or_at_cursor` (bài `>= cursor`): **48/50** → chỉ còn 2 bài cũ hơn cursor (1 pushed + 1 discovery) là truy cập được;
- **36+ bài discovery chưa xem đều MỚI HƠN cursor → vĩnh viễn không được trả về.**

Người dùng đăng nhập chỉ thấy được ~16/50 bài ⇒ đúng triệu chứng.

---

## 4. Điều kiện phát sinh (tổng quát)

Lỗi xuất hiện khi **đuôi của personal track (pushed/KOL/normal) rơi xuống sâu về phía dưới timeline** so với phần discovery chưa xem:

- User follow đủ nhiều / follow phải người sở hữu bài quá cũ;
- Hoặc personal track có hơn `effectiveLimit` bài trong vùng thời gian mới → discovery liên tục bị cắt (`subList(0, limit)` loại bỏ phần discovery trộn vào) trong khi cursor đi sâu dần.

Ngược lại, user không follow / follow ít (đủ thấp để push không lấp kín trang 1) thì discovery xuất hiện sớm, không bị lỗi — do đó bug chỉ bộc lộ với một số tài khoản cụ thể.

---

## 5. Phân tích thiết kế & tác động phụ

1. **Dùng một cursor duy nhất cho nhiều nguồn là không đúng ngữ nghĩa.** Cursor keyset mô tả "vị trí duyệt của TỪNG kênh nguồn dữ liệu". Khi nhiều kênh trộn (follow + discovery), việc dùng vị trí của kênh follow để đọc kênh discovery sẽ tạo "lỗ hổng bỏ sót" ở phía mới hơn cursor.
2. **Discovery bị loại khỏi trang khi personal đủ đầy** — hoàn toàn đúng thiết kế ("chỉ lấp khi hết bài follow"), nhưng hệ quả là các bài discovery không hề được "đánh dấu đã xem" → chúng mới hơn cursor và không thể tiếp cận sau seam.
3. **`limit` không đồng nhất (frontend).** SSR tải trang 1 với `limit=10`, nhưng `loadMore` gọi `limit=2` (`src/routes/(app)/+server.ts` + `+page.svelte`) → mỗi lượt scroll chỉ trả 2 bài, cần ~25 lượt mới hết feed; càng làm triệu chứng "không lướt được nhiều" rõ thêm.
4. **Không phải lỗi SQL.** Discovery SQL tự chạy trả đủ dữ liệu; `loadDiscoveryFeed` (chưa đăng nhập) hoạt động đúng — vấn đề nằm ở tầng logic trộn nguồn + cursor trong `loadFeedByUserId`.

---

## 6. Đề xuất hướng sửa (chưa triển khai — chỉ là định hướng)

Giữ nguyên thiết kế **"discovery chỉ lấp khi hết bài follow"**:

- Sau các tầng personal (pushed/KOL/normal), nếu **pool ≥ effectiveLimit** → giữ nguyên hành vi (không trộn discovery).
- Nếu **pool < effectiveLimit** (điểm nối "seam", personal sắp/cạn):
  - Lần đầu đụng seam: fill `findLatestPublicDiscovery` với exclude = `pool ∪ toàn bộ bài personal user đã thấy` (lấy từ đầu — bài mới nhất chưa xem), thiết lập **`discoveryCursor` riêng** cho trang.
  - Các trang sau seam: duyệt **độc lập track discovery** theo `discoveryCursor` của riêng nó (không phụ thuộc cursor personal), đảm bảo không trùng lặp và không bỏ sót bài mới.
- Tùy chọn: nâng `loadMore` lên `limit=10` để lướt mượt, giảm số request.
- Bổ sung unit test: (a) bật seam khi personal < limit; (b) không trùng ID giữa các trang quanh seam; (c) đủ tất cả bài discovery sau seam.

---

## 7. Cách xác minh

1. Chạy lại bộ test `src/test/java/com/nbh/edushare/modules/feed/query/FeedQueryServiceImplTest.java`.
2. Mô phỏng SQL page 1→4 cho user5 (id=6) như mục 3.2 để đối chiếu.
3. Tái tạo trên app (login user5): lướt bảng tin cho tới lúc chạm seam, xác nhận discovery bắt đầu xuất hiện và feed đủ toàn bộ bài đăng (không còn "Đã hết dữ liệu" sớm).