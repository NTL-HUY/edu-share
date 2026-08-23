package com.nbh.edushare.modules.knowledge.repository;


import com.nbh.edushare.modules.knowledge.pojo.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Category đã có dữ liệu, bỏ qua seed.");
            return;
        }

        List<Category> categories = List.of(
                Category.builder().name("Lập trình Web").build(),
                Category.builder().name("Cấu trúc dữ liệu & Giải thuật").build(),
                Category.builder().name("Cơ sở dữ liệu").build(),
                Category.builder().name("Trí tuệ nhân tạo").build(),
                Category.builder().name("DevOps & Cloud").build(),
                Category.builder().name("Mobile Development").build(),
                Category.builder().name("An toàn thông tin").build(),
                Category.builder().name("Toán ứng dụng CNTT").build()
        );

        categoryRepository.saveAll(categories);
        log.info("Đã seed {} category.", categories.size());
    }
}
