package com.nbh.edushare.modules.knowledge;

import com.nbh.edushare.modules.knowledge.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReferenceDataGraphQLController {
    private final ReferenceDataService referenceDataService;

    @QueryMapping
    public List<CategoryResponse> categories(){
        return referenceDataService.categoryResponseList();
    }

    @QueryMapping
    public List<Map<String, String>> lessonLevels() {
        return referenceDataService.getLessonLevels();
    }
}
