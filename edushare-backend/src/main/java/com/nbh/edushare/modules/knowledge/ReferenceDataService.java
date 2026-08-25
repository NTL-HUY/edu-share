package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.modules.knowledge.dto.response.CategoryResponse;

import java.util.List;
import java.util.Map;

public interface ReferenceDataService {
    List<CategoryResponse> categoryResponseList();

    List<Map<String, String>> getLessonLevels();
}
