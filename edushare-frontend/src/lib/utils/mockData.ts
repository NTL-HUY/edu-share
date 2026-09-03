export const contentMarkDown = `# 🚀 Hướng Dẫn Tích Hợp Spring Boot & GraphQL

Trong bài viết này, chúng ta sẽ tìm hiểu cách triển khai GraphQL API bằng **Spring Boot 3** kết hợp với **SvelteKit**.

> 💡 **Lưu ý:** Bạn cần cài đặt Java 21+ và Node.js v20+ trước khi thực hiện.

---

## 1. Cấu hình Dependencies (\`pom.xml\`)

Thêm đoạn dependency sau vào dự án Spring Boot:

\`\`\`xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
\`\`\`

---

## 2. Bảng So Sánh REST vs GraphQL

| Tiêu chí | REST API | GraphQL API |
| :--- | :--- | :--- |
| **Fetching** | Over-fetching / Under-fetching | Lấy chính xác dữ liệu cần |
| **Endpoint** | Nhiều endpoints (\`/users\`, \`/posts\`) | Duy nhất 1 endpoint (\`/graphql\`) |

---

## 3. Mã Nguồn GraphQL Resolver

\`\`\`java
@Controller
public class FeedController {

    @QueryMapping
    public List<FeedItem> listFeedItems(@Argument String category) {
        return List.of(
            new FeedItem("1", "GraphQL Basics", "Intro to GraphQL"),
            new FeedItem("2", "Svelte 5 Runes", "New state management")
        );
    }
}
\`\`\`

---

## 4. Danh Sách Việc Cần Làm

- [x] Tạo Schema GraphQL (\`schema.graphqls\`)
- [x] Triển khai Query & Mutation Resolvers
- [ ] Tích hợp JWT Authentication
`