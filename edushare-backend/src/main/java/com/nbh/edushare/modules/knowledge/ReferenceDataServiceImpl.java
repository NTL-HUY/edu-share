package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.modules.knowledge.dto.response.CategoryResponse;
import com.nbh.edushare.modules.knowledge.enums.LessonLevel;
import com.nbh.edushare.modules.knowledge.mapper.CategoryMapper;
import com.nbh.edushare.modules.knowledge.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReferenceDataServiceImpl implements ReferenceDataService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public List<CategoryResponse> categoryResponseList() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse).toList();
    }

    @Override
    public List<Map<String, String>> getLessonLevels() {
        return Arrays.stream(LessonLevel.values())
                .map(level -> Map.of(
                        "code", level.name(),
                        "displayName", level.getDisplayName()
                ))
                .toList();
    }
}
