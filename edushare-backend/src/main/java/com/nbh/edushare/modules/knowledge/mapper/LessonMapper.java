package com.nbh.edushare.modules.knowledge.mapper;

import com.nbh.edushare.modules.knowledge.dto.command.CreateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.command.UpdateLessonCommand;
import com.nbh.edushare.modules.knowledge.dto.response.LessonDetailResponse;
import com.nbh.edushare.modules.knowledge.pojo.Lesson;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface LessonMapper {

    @Mapping(target = "type", constant = "LESSON")
        // ép cứng type cho entity con Lesson
    Lesson toEntity(CreateLessonCommand command);

    LessonDetailResponse toDetailResponse(Lesson lesson);

    void updateEntityFromCommand(UpdateLessonCommand command, @MappingTarget Lesson lesson);
}
