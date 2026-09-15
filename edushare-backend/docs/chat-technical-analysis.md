# Phân tích kỹ thuật siêu chi tiết — Module Chat

> Phạm vi: `edushare-backend/src/main/java/com/nbh/edushare/modules/chat`
> Bao gồm các config liên quan: `WebsocketConfig`, `StompChannelConfig`, `KafkaConfig`, `UserHandshakeInterceptor`, `JwtService`.

---

## 1. Tổng quan kiến trúc

Module chat được thiết kế theo mô hình **"Real-time Ingest + Async Persistence"** (inbox thời gian thực đi qua Kafka, ghi DB bất đồng bộ ở consumer). Có **2 luồng vào hoàn toàn tách biệt**:

| Luồng | Entry point | Giao thức | Đặc tính |
|---|---|---|---|
| **A. Real-time** | `ChatStompController` (`@MessageMapping`) | STOMP over WebSocket + Kafka | Fire-and-forget, ACK qua queue, không chờ result |
| **B. CRUD** | `ChatGraphQLController` (GraphQL) | HTTP (GraphQL-over-HTTP) | Request-response, trả entity trực tiếp |

**Lý do tách 2 luồng:** tin nhắn real-time cần độ trễ thấp và khả năng mất tin thấp (Kafka như write-ahead log). API fallback dùng cho trường hợp không có WebSocket hoặc gọi từ client muốn kết quả đồng bộ.

---

## 2. Danh sách thành phần & vai trò

| Thành phần | Loại | Vai trò |
|---|---|---|
| `ChatStompController` | STOMP controller | Nhận frame `send /app/chat.send` | 
| `ChatService` / `ChatServiceImpl` | Service | Nghiệp vụ chính: send, markRead, delete, unread, paging |
| `ChatWebSocketService` / Impl | Service | Bọc `SimpMessagingTemplate` để gửi ACK cá nhân |
| `ChatMessageListener` | Kafka listener | Consumer ghi DB + broadcast realtime |
| `ChatKafkaConfig` | Constants | Topic + group id |
| `ChatMapper` | MapStruct | DTO ↔ Event / Event → Entity |
| `ChatMessageBuilder` | Util/Component | Xây entity, validate reply, tạo preview |
| `ChatGraphQLController` | GraphQL controller | Query/Mutation/BatchMapping |
| `ChatMessage`, `ChatRoom`, `RoomReadState` | JPA Entity | Lưu trữ |
| `ChatMessageRepository`, `ChatRoomRepository`, `RoomReadStateRepository` | Repository | Truy vấn |
| `IncomingChatMessage`, `SendMessageRequest`, `CursorPaginateRequest` | Request DTO | Input + bean validation |
| `ChatAckMessage`, `CursorPagingResponse`, `NewMessageBroadcast` | Response DTO | Output |
| `ChatMessageEvent` | Kafka payload | Event truyền trên topic |
| `AckStatus` | Enum | `PENDING` / `FAILED` / `SENT` |
| `RoomUnreadProjection` | Projection | Projection cho câu đếm unread |

---

## 3. Luồng A — Gửi tin nhắn real-time (STOMP + Kafka)

```
Client STOMP
   │  SEND /app/chat.send  body={clientTempId, roomId, content, replyToMessageId}
   ▼
ChatStompController.handleSend(payload, principal)
   │  principal.getName() = userId (do StompChannelConfig set Principal)
   │  → payload: IncomingChatMessage (bean-validated: content ≤ 2000, roomId > 0)
   ▼
ChatServiceImpl.handleStompSendMessage(payload, userId)   [readOnly tx — KHÔNG hợp lý, xem §9]
   │  ├─ 1. Kiểm tra room: chatRoomRepository.existsByIdAndIsActiveTrue(roomId)
   │  │     ❌ sai → chatWebSocketService.sendAckToUser(userId, clientTempId, FAILED,
   │  │            "Phòng không tồn tại hoặc bạn không có quyền")  và return.
   │  ├─ 2. Map: chatMapper.toChatMessageEvent(payload, userId)
   │  │     → ChatMessageEvent(clientTempId, roomId, userId, content, replyToMessageId,
   │  │                        createdAt = LocalDateTime.now())
   │  └─ 3. kafkaTemplate.send(TOPIC="chat-send-message-events", key=roomId, value=event)
   │         ├─ callback thành công → ACK PENDING (chỉ báo "đã vào hàng đợi")
   │         └─ callback lỗi (broker ném ex) → ACK FAILED "Không gửi được, thử lại"
   ▼
KAFKA  chat-send-message-events  (partitions theo key = roomId → đảm bảo thứ tự trong 1 phòng)
   ▼
ChatMessageListener.onSendMessage(event)   [group = "chat-message-writer", @Transactional]
   │  ├─ 1. IDEMPOTENCY: existsByClientTempIdAndUserId(clientTempId, userId)
   │  │     → nếu đã tồn tại: log WARN + skip (chống duplicate khi Kafka retry)
   │  ├─ 2. userService.findProjectedById(event.userId(), UserBaseProjection.class)
   │  │     ❌ user không tồn tại → throw AppException(USER_NOT_FOUND)
   │  ├─ 3. chatMessageBuilder.build(roomId, user, content, replyToMessageId, clientTempId)
   │  │     → BuildResult (failed nếu reply không hợp lệ)
   │  │     ❌ failed → ACK FAILED + return
   │  ├─ 4. ⚠️ Thread.sleep(5000) — giả lập trễ, đang chờ xóa (xem §9)
   │  ├─ 5. chatMessageRepository.save(msg)   (trong transaction của listener)
   │  └─ 6. messagingTemplate.convertAndSend("/topic/room-{roomId}", saved)
   │        → tất cả client subscribe room nhận được entity ChatMessage
```

### 3.1. Xác thực STOMP (2 lớp)

**Lớp 1 — Handshake:** `UserHandshakeInterceptor.beforeHandshake` đọc cookie
`access_token` từ HTTP request của handshake (`/ws-chat`) → `jwtService.verifyAndParseAccessToken(token)` → gắn `tokenPayload` vào session attributes. Không có cookie = handshake bị hủy (`return false`).

**Lớp 2 — INBOUND channel:** `StompChannelConfig.configureClientInboundChannel` chặn mọi message đi vào broker. Khi gặp frame `CONNECT`, đọc `tokenPayload` từ session attributes → `accessor.setUser((Principal) tokenPayload::userId)`.

> Điểm tinh tế: `Principal` được implement bằng **method reference** `tokenPayload::userId` — một lambda `Supplier<String>` được giả danh là `Principal` (mọi `getName()` trả về userId dạng String). Đây là mẹo "ad-hoc principal" để Spring Messaging tự động inject vào `@MessageMapping(Principal)` mà không cần class riêng.

**GraphQL/Kafka context:** `ChatGraphQLController` lấy user từ `SecurityContextHolder.getContext().getAuthentication().getPrincipal()` (cast sang `Long`) hoặc `@AuthenticationPrincipal Long userId` — nghĩa là SecurityFilter đặt principal = userId (Long).

### 3.2. Fire-and-forget & ACK pattern

- Listener **không trả kết quả**: `@MessageMapping` method trả `void` → client không nhận response frame trực tiếp.
- Thay vào đó dùng **asynchronous acknowledgment** qua user queue:
  - `sendAckToUser(userId, clientTempId, status, reason)` → `convertAndSendToUser(userId, "/queue/ack", ChatAckMessage)`.
  - Client subscribe `/user/queue/ack` (WebSocketConfig set user prefix `/user`) → nhận ACK cá nhân.
- **`clientTempId`** đóng vai trò correlation key: client tự sinh id tạm để nối ACK/broadcast với tin mình vừa gửi (dựa trường này client render tin "PENDING" rồi chuyển sang "SENT" khi nhận broadcast).

---

## 4. Luồng B — CRUD qua GraphQL

### 4.1. `sendMessage` (mutation, đồng bộ, không broadcast)

```
sendMessage(roomId, request, @AuthenticationPrincipal Long userId)
  → findByIdAndIsActiveTrue(roomId)      ❌ room không active → AppException
  → userService.findProjectedById(userId, UserBaseProjection)  ❌ → USER_NOT_FOUND
  → chatMessageBuilder.build(roomId, user, content, replyToMessageId, null)  // clientTempId=null
  → chatMessageRepository.save(result.message())  → trả entity
```
Điểm khác biệt với luồng A: không có idempotency key (clientTempId null), không qua Kafka, không broadcast. Role như "đường hậu bị" hoặc cho tab API-đơn-thuần.

### 4.2. `markRead` — khoá "chỉ tăng" (monotonic last-read cursor)

```
markRead(roomId, userId, messageId)
  → userService.findProjectedById(userId, ...)            ❌ → USER_NOT_FOUND
  → roomReadStateRepository.updateLastReadIfGreater(roomId, uid, messageId)
       UPDATE RoomReadState r
       SET r.lastReadMessageId = :messageId
       WHERE r.roomId=:roomId AND r.userId=:userId
         AND (r.lastReadMessageId IS NULL OR r.lastReadMessageId < :messageId)
       → trả số dòng cập nhật
  → nếu updated == 0 và chưa tồn tại state → INSERT row mới (lastRead = messageId)
```
Kỹ thuật: **compare-and-set kiểu optimistic monotonic** — chỉ cho phép cursor tiến về phía trước; gửi messageId cũ hơn = no-op. Điều kiện `lastReadMessageId IS NULL OR < :id` chống cả 2 trường hợp: row mới & đọc lùi. Unique constraint `uk_room_read_state_room_user(room_id, user_id)` là lưới an toàn chống 2 insert cùng lúc (nếu va chạm → DataIntegrityViolationException).

### 4.3. `deleteMessage` — soft delete + phân quyền

```
deleteMessage(roomId, userId, messageId)
  → findById(messageId) filter (roomId khớp)   ❌ → "Tin nhắn không tồn tại"
  → nếu deletedAt != null → return ngay (idempotent, tin đã xóa)
  → userService.findProjectedById(userId, UserRoleProjection)
  → isOwner = userId của message == userId hiện tại
    isAdmin = userRole == ADMIN
  → ❌ không owner && không admin → AppException "Bạn không có quyền xóa..."
  → message.setDeletedAt(now); message.setDeletedBy(currentUser.getId()); save
```
Kỹ thuật: **soft delete với audit trail** (`deletedAt`, `deletedBy`). Entity kế thừa `SoftDeleteModel`. Truyền thống dùng `@SQLDelete`/`@Where` để filter tự động, nhưng codebase này filter bằng JPQL thủ công (`m.deletedAt IS NULL` trong câu unread) và findBy ở một số nơi không filter deleted — cần kiểm soát đồng bộ.

### 4.4. `messages` — Cursor-based pagination (keyset pagination)

```
getMessages(roomId, request{CursorPaginateRequest(beforeId, limit)})
  → Pageable = PageRequest.of(0, limit + 1)     // lấy dư 1 để biết hasMore
  → chatMessageRepository.findMessagesByCursor(roomId, beforeId, pageable)
       WHERE m.roomId=:roomId AND (:beforeId IS NULL OR m.id < :beforeId)
       ORDER BY m.id DESC
  → hasMore = items.size() > limit → nếu có, cắt subList(0, limit)
  → nextBeforeId = items.getLast().getId()  (nếu có item) → cursor cho trang kế
  → CursorPagingResponse<ChatMessage>(items, beforeId, hasMore)
```
Ưu điểm so với OFFSET pagination: dùng chỉ số primary key `id` (so sánh bất đẳng thức) — **query chạy index, O(1) nhảy trang**, bền vững khi dữ liệu mới chèn liên tục, không bị "trang nhảy" khi có insert mới. Khuyết: chỉ duyệt theo một chiều (giảm dần theo id), không nhảy đến trang N.

### 4.5. `unreadCount` — `@BatchMapping` chống N+1

```java
@BatchMapping(typeName = "ChatRoom")
public Map<ChatRoom, Long> unreadCount(List<ChatRoom> rooms) {
    Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Map<Long, Long> countsByRoomId = chatService.getUnreadCounts(userId);  // 1 query cho tất cả phòng
    return rooms.stream().collect(Collectors.toMap(room -> room,
        room -> countsByRoomId.getOrDefault(room.getId(), 0L)));
}
```
- Spring GraphQL `@BatchMapping` gom mọi yêu cầu resolve field `unreadCount` của tất cả `ChatRoom` trong 1 lần gọi → **1 query unread duy nhất (đã GROUP BY roomId)** thay vì N query → tránh N+1 khi client query nhiều phòng.
- `Collectors.toMap` + `getOrDefault` đảm bảo trả 0 cho phòng không có bản ghi.

### 4.6. Câu đếm unread tổng hợp

```sql
SELECT m.roomId, COUNT(m.id) AS unreadCount
FROM ChatMessage m
LEFT JOIN RoomReadState rrs
    ON rrs.roomId = m.roomId AND rrs.userId = :userId
WHERE m.deletedAt IS NULL
  AND m.id > COALESCE(rrs.lastReadMessageId, 0)
GROUP BY m.roomId
```
Kỹ thuật: **LEFT JOIN để tính cursor mặc định** — user chưa từng mở phòng (`rrs = NULL`) thì `COALESCE(...,0)`: mọi tin có id > 0 (= tất cả) tính là chưa đọc; tin đã soft-delete bị loại qua `m.deletedAt IS NULL`. Chỉ một phát `GROUP BY` cho toàn bộ user. Kết quả dưới dạng **interface projection** `RoomUnreadProjection` (Spring Data trả proxy implement interface — không select toàn entity).

---

## 5. Ghi & chuẩn hoá dữ liệu

### 5.1. Entity `ChatMessage` — denormalization chủ động

| Cột | Ý nghĩa | Ghi chú |
|---|---|---|
| `room_id`, `user_id` | FK logic | Không có `@ManyToOne` real FK |
| `user_name`, `user_avatar_url` | **Snapshot user** | Denormalize để broadcast/query không cần join |
| `client_temp_id` | Idempotency key | NOT NULL trong luồng Kafka (nullable ở GraphQL path) |
| `reply_to_message_id` | Tin gốc được reply | |
| `reply_to_user_name`, `reply_to_content_preview` | **Snapshot reply** | Denormalize để render preview không join |
| `content` | TEXT | tối đa 2000 (STOMP) / 5000 (GraphQL) |

Kỹ thuật denormalize: bỏ qua join, chịu trách nhiệm giữ đồng bộ bằng cách **ghi snapshot ngay lúc tạo tin nhắn** (trong `ChatMessageBuilder`). Đánh đổi: dung lượng tăng nhẹ, giữa thao tác đổi username/avatar cũ thì tin nhắn cũ giữ nguyên ảnh cũ — chấp nhận được, ghi nhất quán tại thời điểm gửi.

### 5.2. `ChatMessageBuilder` — validators + builder pattern

```java
BuildResult build(roomId, sender(UserBaseProjection), content, replyToMessageId, clientTempId)
  message.setUserName(sender.getUsername()); message.setUserAvatarUrl(sender.getAvatarUrl())
  if (replyToMessageId != null) {
      original = chatMessageRepository.findById(replyToMessageId)
          .filter(deletedAt == null)      // chỉ reply tin chưa xóa
          .filter(roomId của gốc == roomId hiện tại)  // chống reply xuyên phòng
          .orElse(null);
      if (original == null) return BuildResult.failed("Tin nhắn để phản hồi không tồn tại");
      message.setReplyToMessageId(original.getId());
      message.setReplyToUserName(original.getUserName());
      message.setReplyToContentPreview(buildPreview(original.getContent())); // 100 ký tự + "..."
  }
  return BuildResult.success(message);
```

Kỹ thuật:
- **Result object pattern** (`BuildResult(message, errorReason)`, `isFailed()`, factory `success()/failed()`) — tránh exception cho validation nghiệp vụ, giúp listener map-sang ACK FAILED gọn gàng.
- **Buffer truncation**: `buildPreview` cắt chuỗi + `"..."`; tránh mất ký tự Unicode giữa chuỗi (dùng substring theo UTF-16 — vẫn có rủi ro cắt đôi surrogate pair cho emoji, xem §9).
- **Double-validation reply**: cả luồng A (listener) và luồng B (sendMessage) đều dùng chung builder → logic không trùng lặp.

---

## 6. Kỹ thuật MapStruct — mapping tầng DTO/Event/Entity

`ChatMapper` (interface, `componentModel = "spring"` → sinh `ChatMapperImpl`):
- `toChatMessageEvent(IncomingChatMessage, Long userId)`:
  - map `roomId`, `content`, `replyToMessageId`, `clientTempId` theo tên,
  - `@Mapping(target="createdAt", expression="java(LocalDateTime.now())")` — **expression mapping** chèn thời điểm nhận tin (dedupe thời gian server, không tin tưởng client), userId map từ tham số.
- `fromChatMessageEvent(ChatMessageEvent)` → về `ChatMessage` nhưng **ignore toàn bộ audit/denormalize fields** (`id`, `createdAt`, `updatedAt`, `deletedAt`, `deletedBy`, `userName`, `userAvatarUrl`, `replyToUserName`, `replyToContentPreview`). Tuy nhiên method này hiện **không được dùng** — listener tự build entity qua `ChatMessageBuilder` (có snobody snapshot). Có thể coi đây là đường mapping dự phòng.

---

## 7. Kafka — cấu hình & quy ước

### 7.1. Producer (`KafkaConfig`)
- `JsonSerializer` cho value; producer ném object bất kỳ (`KafkaTemplate<String, Object>`).
- `ACKS = all` → confirm sau khi tất cả replicas ghi — hi sinh latency để chắc chắn không mất tin.
- `RETRIES = 3` → producer tự retry lỗi tạm (broker down, network) trước khi trả lỗi về callback.
- **Key = `roomId.toString()`** (`kafkaTemplate.send(topic, payload.roomId().toString(), event)`):
  - Kafka đảm bảo **thứ tự theo key trong 1 partition** → mọi tin nhắn của cùng 1 phòng đi một partition → **giữ nguyên trật tự gửi – lưu – broadcast** cho mỗi phòng, không bị đảo thứ tự khi concurrency.

### 7.2. Consumer
- `JsonDeserializer` với **`TRUSTED_PACKAGES = "*"`** (mở broad — mối lo bảo mật, §9), tự đưa header type-info.
- `AUTO_OFFSET_RESET = earliest` → app mới join topic sẽ đọc lại từ đầu (nếu group mới) — không mất event khi listener chưa kịp lên.
- **Concurrency = 3** cho `ConcurrentKafkaListenerContainerFactory` → spark nhiều consumer thread (tối đa 3 partitions được tiêu thụ song song).
- `DefaultErrorHandler(new FixedBackOff(2000, 3))` → khi consumer throw (VD: AppException USER_NOT_FOUND ở listener): **retry 3 lần, cách 2 giây**, sau đó gửi tới DLT/đánh dấu failed (mặc định drop). Kết hợp với idempotency `clientTempId` để retry an toàn.

### 7.3. Topic & Consumer group
- `CHAT_SEND_MESSAGE_TOPIC = "chat-send-message-events"`
- `CHAT_MESSAGE_WRITER = "chat-message-writer"` — group này chứa đúng 1 listener → theo **consumer group pattern**, mỗi event chỉ được xử lý bởi 1 instance; scale nhiều app instance = thêm 1 consumer cùng group, mỗi instance nhận subset partitions.

---

## 8. WebSocket — cấu hình broker

```java
@EnableWebSocketMessageBroker
registerStompEndpoints: /ws-chat  (handshake interceptor + AllowedOriginPatterns("*"))
configureMessageBroker:
    enableSimpleBroker("/topic", "/queue")   // in-memory broker
    setApplicationDestinationPrefixes("/app") // client gửi tới /app/...
    setUserDestinationPrefix("/user")         // convertAndSendToUser → /user/{id}/queue/...
```

| Chuyện gì xảy ra | Dest |
|---|---|
| Client gửi tin | `send → /app/chat.send` |
| Broadcast tin mới | `convertAndSend → /topic/room-{roomId}` (mọi client trong room) |
| ACK cá nhân | `convertAndSendToUser(userId, /queue/ack)` → client nghe `/user/queue/ack` |

Simple broker = **single-node in-memory**: không dùng RabbitMQ/ActiveMQ bên ngoài; nếu chạy nhiều app instance, broadcast chỉ tới client đang kết nối instance đó — cần sticky session hoặc nâng cấp broker nếu scale ngang (xem §9).

---

## 9. Điểm yếu, rủi ro, và khuyến nghị

### 🔴 Nghiêm trọng

1. **`Thread.sleep(5000)` trong listener** (`ChatMessageListener.java:58`) — giữ 1 consumer-thread + 1 transaction JPA trong 5s mỗi tin; nhân với concurrency=3 là bottleneck; nếu 2 tin/giây sẽ ngập hàng đợi, delay tăng vô hạn. → **Xóa ngay** hoặc thay bằng batch (chunk 50ms – 100 tin) + producer-side bulk.
2. **Broadcast gửi thẳng entity `ChatMessage`** thay vì `NewMessageBroadcast` — broadcast từ Kafka listener thread, entity từ **Transaction 1**, sau khi listener transaction commit (mặc định) entity có thể đã detached; các field lazy sẽ lỗi khi serialize trên thread khác. → Dùng `NewMessageBroadcast.from(saved)` (static factory đã có sẵn nhưng bị bỏ quên).
3. **`sendMessage` (GraphQL) không broadcast** — client đang mở WebSocket sẽ không thấy tin gửi qua GraphQL; nếu cả 2 đường song hành → mất đồng bộ UI. → Gộp về 1 con đường (nên qua Kafka chung để có idempotency + thứ tự).

### 🟠 Trung bình

4. `markRead`: `findByRoomIdAndUserId(...).isPresent()` vẫn có thể race (2 request cùng lúc) → nhưng unique constraint bắt được; nên bắt `DataIntegrityViolationException` và ignore thay vì để exception 500. Đồng thời thiếu `@Transactional` tách — hiện cả method đang chung chuyện.
5. **ACK `SENT` không bao giờ gửi** — client chỉ thấy PENDING/FAILED; không biết khi nào tin thực sự lên hệ thống. Listener nên gửi ACK SENT sau save (hoặc để client tự suy ra từ broadcast `NewMessageBroadcast.clientTempId`).
6. `handleStompSendMessage` đánh dấu `@Transactional(readOnly = true)` nhưng thực hiện **I/O (Kafka send)** — không có tác dụng gì ngoài việc tạo DB connection; gây nhầm lẫn.
7. **`TRUSTED_PACKAGES = "*"`** + `JsonDeserializer` mở rộng — nguy cơ deserialization gadget nếu broker bị xâm nhập. → Giới hạn về `com.nbh.edushare.*`.
8. **CORS `setAllowedOriginPatterns("*")`** — cho phép mọi origin; nên thay bằng danh sách origin cụ thể.
9. `ChatStompController` import thừa `KafkaTemplate` (dead import); `extractUserId` trong `UserHandshakeInterceptor` là dead code.

### 🟡 Nhẹ / Khuyến nghị

10. `MAX_LIMIT`/`PREVIEW_MAX_LENGTH` trong `ChatServiceImpl` khai nhưng không dùng (`buildPreview` + `MAX_LIMIT` là dead code); chuẩn hoá: giữ `ChatMessageBuilder` làm place duy nhất cho preview.
11. Paging không ép `limit ≤ 100` ở service (validation nằm ở `@Size` tầng request chưa có) — thêm clamp `Math.min(limit, MAX_LIMIT)`.
12. `buildPreview` dùng `substring` (UTF-16) có thể cắt đứt đôi surrogate pair (emoji) → preview vỡ ký tự. Dùng `OffsetByCodePoints` hoặc thư viện (ICU).
13. `ChatMessage.userName`/`avatarUrl`/`replyToUserName` filterfiche bảo mật: snapshot được trả nguyên trong broadcast — kiểm tra xem có nên ẩn (VD: avatar private) không.
14. Không có **message seen receipts** (đã xem chưa), typing indicator, hay **online presence** — nếu roadmap cần, thiết kế thêm event topic riêng.
15. `isActive` để lọc room; **thiếu membership table** — bất kỳ user nào biết roomId cũng gửi được. Nếu cần quyền thành viên → thêm `chat_room_member` và kiểm tra trong `handleStompSendMessage`.
16. `getChatRooms()` trả toàn bộ rooms active mà không phân trang theo user.

---

## 10. Tóm tắt kỹ thuật chủ chốt

- **Client-generated temp ID như idempotency key phòng retry Kafka.**
- **Event-driven write-behind**: STOMP chỉ ACK PENDING, Kafka đảm bảo durability+thứ tự, consumer ghi DB.
- **Keyed Kafka = per-room ordering** (key = roomId → một partition → thứ tự tin).
- **Consumer group & concurrency** cho scale-out đa instance.
- **AckStatus lifecycle** PENDING → FAILED / (nên có) SENT.
- **Monotonic last-read cursor** cho đánh dấu đã đọc + `@Modifying` bulk update.
- **LEFT JOIN + COALESCE** cho unread count một query.
- **Cursor/keyset pagination** bằng `id < :beforeId`, `limit+1` detector.
- **Soft delete có audit** (`deletedAt`, `deletedBy`) + phân quyền admin/owner.
- **Denormalize snapshot** (người gửi + reply preview) để render không join.
- **MapStruct mapping + expression** cho server timestamp.
- **`@BatchMapping` GraphQL** chống N+1 khi resolve unread trên nhiều phòng.
- **Projection interfaces** để query gen select tối thiểu.
- **Result-object pattern** cho validation nghiệp vụ, tách biệt với exception hệ thống.
- **Hardcode principal qua method reference lambda** trong STOMP inbound channel.