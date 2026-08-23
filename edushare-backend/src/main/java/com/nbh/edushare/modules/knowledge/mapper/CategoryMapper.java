package com.nbh.edushare.modules.knowledge.mapper;

import com.nbh.edushare.modules.knowledge.dto.response.CategoryResponse;
import com.nbh.edushare.modules.knowledge.pojo.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
}
